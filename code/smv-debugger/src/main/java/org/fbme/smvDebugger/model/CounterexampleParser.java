package org.fbme.smvDebugger.model;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import jetbrains.mps.internal.collections.runtime.ListSequence;

public class CounterexampleParser {
  private static final String STATE_TRASH_SYMBOLS = "-1[.]";
  private static final String DATA_DELIMITER = ",";

  private final SystemItemParser itemParser = new SystemItemParser();

  public Counterexample parse(final List<String> lines) {
    final String[] emptyHeaderAndSteps = lines.get(0).replaceAll(STATE_TRASH_SYMBOLS, "").split(DATA_DELIMITER);
    final String[] steps = Arrays.copyOfRange(emptyHeaderAndSteps, 1, emptyHeaderAndSteps.length);

    final List<SystemItem> items = new ArrayList<SystemItem>();
    final List<String[]> values = new ArrayList<String[]>();
    String[] timeValues = new String[steps.length];
    for (int i = 1; i < lines.size(); i++) {
      final String[] lineData = lines.get(i).split(DATA_DELIMITER);
      String fullName = lineData[0];
      final String[] curValues = Arrays.copyOfRange(lineData, 1, lineData.length);

      if (Objects.equals(fullName, Counterexample.T_GLOBAL)) {
        timeValues = curValues;
        continue;
      }

      final SystemItem item = itemParser.parse(fullName);
      if (item != null) {
        ListSequence.fromList(items).addElement(item);
        ListSequence.fromList(values).addElement(curValues);
      }
    }

    return new Counterexample(ListSequence.fromList(items).toGenericArray(SystemItem.class), steps, ListSequence.fromList(values).toGenericArray(String[].class), timeValues);
  }
}
