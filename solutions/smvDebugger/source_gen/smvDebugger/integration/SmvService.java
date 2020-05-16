package smvDebugger.integration;

/*Generated by MPS */

import smvDebugger.model.CounterexampleParser;
import smvDebugger.plugin.SmvBinaryPaths_PreferencesComponent;
import java.nio.file.Path;
import java.util.Optional;
import smvDebugger.model.Counterexample;
import java.nio.file.Files;
import java.util.List;
import java.io.IOException;

public class SmvService {
  private final Fb2SmvService fb2SmvService;
  private final NuSmvService nuSmvService;
  private final NutracService nutracService;

  private final CounterexampleParser counterexampleParser;

  public SmvService(final SmvBinaryPaths_PreferencesComponent smvBinaryPaths) {
    fb2SmvService = new Fb2SmvService(Path.of(smvBinaryPaths.getStateObject().fb2SmvPath));
    nuSmvService = new NuSmvService(Path.of(smvBinaryPaths.getStateObject().nuSmvPath));
    nutracService = new NutracService(Path.of(smvBinaryPaths.getStateObject().nutracPath));

    counterexampleParser = new CounterexampleParser();
  }

  public Optional<Counterexample> verify(final Path fbPath, final String specification) {
    try {
      final Path smvPath = fb2SmvService.convertFbToSmv(fbPath);

      final Optional<Path> rawCounterexamplePath = nuSmvService.getRawCounterexample(smvPath, specification);
      Files.delete(smvPath);

      if (rawCounterexamplePath.isEmpty()) {
        return Optional.empty();
      }

      final Path csvCounterexamplePath = nutracService.convertToCsv(rawCounterexamplePath.get());
      Files.delete(rawCounterexamplePath.get());

      final List<String> lines = Files.readAllLines(csvCounterexamplePath);
      Files.delete(csvCounterexamplePath);

      final Counterexample counterexample = counterexampleParser.parse(lines);
      return Optional.of(counterexample);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
