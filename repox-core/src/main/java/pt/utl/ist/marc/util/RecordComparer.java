/*
 * Created on 28/Jun/2005
 *
 */
package pt.utl.ist.marc.util;

import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;
import pt.utl.ist.util.structure.Tuple;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Nuno Freire
 * 
 */
public class RecordComparer {
    private Set<Integer> fieldsToIgnore;
    private boolean      ignoreCase = true;

    /**
	 */
    public enum RecordEngulfing {
        /** RecordEngulfing None */
        None,
        /** RecordEngulfing AcontainsB */
        AcontainsB, 
        /** RecordEngulfing BcontainsA */
        BcontainsA, 
        /** RecordEngulfing Equal */
        Equal
    }

    /**
     * Creates a new instance of this class.
     */
    public RecordComparer() {
        fieldsToIgnore = new HashSet<Integer>();
        fieldsToIgnore.add(0);
        fieldsToIgnore.add(1);
        fieldsToIgnore.add(5);
        fieldsToIgnore.add(035);
        fieldsToIgnore.add(966);
    }

    /**
     * Creates a new instance of this class.
     * @param ignoreCase
     */
    public RecordComparer(boolean ignoreCase) {
        this();
        this.ignoreCase = ignoreCase;
    }

    /**
     * Creates a new instance of this class.
     * @param fieldsToIgnore
     */
    public RecordComparer(Set<Integer> fieldsToIgnore) {
        this.fieldsToIgnore = fieldsToIgnore;
    }

    /**
     * Creates a new instance of this class.
     * @param fieldsToIgnore
     * @param ignoreCase
     */
    public RecordComparer(Set<Integer> fieldsToIgnore, boolean ignoreCase) {
        this.fieldsToIgnore = fieldsToIgnore;
        this.ignoreCase = ignoreCase;
    }

    /**
     * @param recA
     * @param recB
     * @return boolean indicating if the records are equal
     */
    public boolean areEqual(Record recA, Record recB) {
        int idxA = 0;
        int idxB = 0;

        if (!fieldsToIgnore.contains(0)) {
            if (!leadersAreEquals(recA.getLeader(), recB.getLeader())) { return false; }
        }

        boolean finished = false;
        while (!finished) {
            Field fldA = null;
            Field fldB = null;
            int tagA = 0;
            int tagB = 0;

            try {
                fldA = recA.getFields().get(idxA);
                tagA = fldA.getTag();
                while (fieldsToIgnore.contains(tagA)) {
                    idxA++;
                    fldA = recA.getFields().get(idxA);
                    tagA = fldA.getTag();
                }
            } catch (IndexOutOfBoundsException e) {
                fldA = null;
                tagA = 1000;
            }

            try {
                fldB = recB.getFields().get(idxB);
                tagB = fldB.getTag();
                while (fieldsToIgnore.contains(tagB)) {
                    idxB++;
                    fldB = recB.getFields().get(idxB);
                    tagB = fldB.getTag();
                }
            } catch (IndexOutOfBoundsException e) {
                fldB = null;
                tagB = 1000;
            }

            if (tagB != tagA) {
                return false;
            } else if (tagA == 1000 || tagB == 1000) {
                return true;
            } else {
                idxA++;
                idxB++;

                if (fldA.isControlField()) {
                    if (!equals(fldA.getValue(), fldB.getValue())) return false;
                } else {
                    if (fldA.getInd1() != fldB.getInd1() || fldA.getInd2() != fldB.getInd2()) return false;
                    for (Subfield sfA : fldA.getSubfields()) {
                        boolean found = false;
                        for (Subfield sfB : fldB.getSubfields()) {
                            if (sfA.getCode() == sfB.getCode() && equals(sfA.getValue(), sfB.getValue())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) { return false; }
                    }

                    for (Subfield sfB : fldB.getSubfields()) {
                        boolean found = false;
                        for (Subfield sfA : fldA.getSubfields()) {
                            if (sfA.getCode() == sfB.getCode() && equals(sfA.getValue(), sfB.getValue())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) { return false; }
                    }
                }
            }
        }

        return true;
    }

    /**
     * @param recA
     * @param recB
     * @return Tupple with V1=% of equal fields, V2=how the records compare
     */
    public Tuple<Integer, RecordEngulfing> compareRecords(Record recA, Record recB) {
        int idxA = 0;
        int idxB = 0;

        int totalFieldsCounter = 0;
        int disparateFieldsCounter = 0;

        RecordEngulfing engulf = RecordEngulfing.Equal;

        boolean finished = false;
        while (!finished) {
            Field fldA = null;
            Field fldB = null;
            int tagA = 0;
            int tagB = 0;

            try {
                fldA = recA.getFields().get(idxA);
                tagA = fldA.getTag();
                while (fieldsToIgnore.contains(tagA)) {
                    idxA++;
                    fldA = recA.getFields().get(idxA);
                    tagA = fldA.getTag();
                }
            } catch (IndexOutOfBoundsException e) {
                fldA = null;
                tagA = 1000;
            }

            try {
                fldB = recB.getFields().get(idxB);
                tagB = fldB.getTag();
                while (fieldsToIgnore.contains(tagB)) {
                    idxB++;
                    fldB = recB.getFields().get(idxB);
                    tagB = fldB.getTag();
                }
            } catch (IndexOutOfBoundsException e) {
                fldB = null;
                tagB = 1000;
            }

            totalFieldsCounter++;
            if (tagB != tagA) {
                disparateFieldsCounter++;
                if (tagA < tagB) {
                    idxA++;
                    switch (engulf) {
                    case Equal:
                        engulf = RecordEngulfing.AcontainsB;
                        break;
                    case BcontainsA:
                        engulf = RecordEngulfing.None;
                        break;
                    }
                } else {
                    idxB++;
                    switch (engulf) {
                    case Equal:
                        engulf = RecordEngulfing.BcontainsA;
                        break;
                    case AcontainsB:
                        engulf = RecordEngulfing.None;
                        break;
                    }
                }
            } else if (tagA == 1000 || tagB == 1000) {
                finished = true;
                totalFieldsCounter--;
            } else {
                idxA++;
                idxB++;
                RecordEngulfing fldComp = compareFields(fldA, fldB);
                switch (fldComp) {
                case Equal:
                    break;
                case AcontainsB:
                    disparateFieldsCounter++;
                    switch (engulf) {
                    case Equal:
                        engulf = RecordEngulfing.AcontainsB;
                        break;
                    case BcontainsA:
                        engulf = RecordEngulfing.None;
                        break;
                    }
                    break;
                case BcontainsA:
                    disparateFieldsCounter++;
                    switch (engulf) {
                    case Equal:
                        engulf = RecordEngulfing.AcontainsB;
                        break;
                    case BcontainsA:
                        engulf = RecordEngulfing.None;
                        break;
                    }
                    break;
                case None:
                    disparateFieldsCounter++;
                    engulf = RecordEngulfing.None;
                    break;
                }
            }
        }

        if (engulf == RecordEngulfing.Equal) return new Tuple<Integer, RecordEngulfing>(100, engulf);
        return new Tuple<Integer, RecordEngulfing>(100 - (disparateFieldsCounter * 100 / totalFieldsCounter), engulf);
    }

    /**
     * @param fA
     * @param fB
     * @return RecordEngulfing
     */
    protected RecordEngulfing compareFields(Field fA, Field fB) {
        if (fA.isControlField()) { return equals(fA.getValue(), fB.getValue()) ? RecordEngulfing.Equal : RecordEngulfing.None; }

        RecordEngulfing engulfBtoA = RecordEngulfing.Equal;

        for (Subfield sfA : fA.getSubfields()) {
            boolean found = false;
            for (Subfield sfB : fB.getSubfields()) {
                if (sfA.getCode() == sfB.getCode() && equals(sfA.getValue(), sfB.getValue())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                engulfBtoA = RecordEngulfing.BcontainsA;
                break;
            }
        }

        RecordEngulfing engulfAtoB = RecordEngulfing.Equal;
        for (Subfield sfB : fB.getSubfields()) {
            boolean found = false;
            for (Subfield sfA : fA.getSubfields()) {
                if (sfA.getCode() == sfB.getCode() && equals(sfA.getValue(), sfB.getValue())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                engulfAtoB = RecordEngulfing.AcontainsB;
                break;
            }
        }

        if (engulfAtoB == RecordEngulfing.Equal && engulfBtoA == RecordEngulfing.Equal) { return RecordEngulfing.Equal; }
        if (engulfAtoB == RecordEngulfing.AcontainsB) { return RecordEngulfing.AcontainsB; }
        if (engulfBtoA == RecordEngulfing.BcontainsA) { return RecordEngulfing.BcontainsA; }
        return RecordEngulfing.None;
    }

    private boolean equals(String sA, String sB) {
        return equals(sA, sB, ignoreCase);
    }

    private static boolean equals(String sA, String sB, boolean ignoreCase) {
        if (ignoreCase) return sA.equalsIgnoreCase(sB);
        return sA.equals(sB);
    }

    /**
     * @param leadA
     * @param leadB
     * @return If the readers are equal
     */
    public static boolean leadersAreEquals(String leadA, String leadB) {
        return equals(leadA.substring(5, 10), leadB.substring(5, 10), false) || !equals(leadA.substring(17, 20), leadB.substring(17, 20), false);
    }
}
