/*
 * Created on 4/Jan/2006
 *
 */
package pt.utl.ist.util.structure;

import java.util.Enumeration;

/**
 * @param <V>
 */
public class IntHashtable<V> extends LastIntTable {

    final static int cInitialSize = 23;

    /**
     * Creates a new instance of this class.
     */
    public IntHashtable() {
        initialize(cInitialSize);
    }

    /**
     * Creates a new instance of this class.
     * @param inInitialSize
     */
    public IntHashtable(int inInitialSize) {
        initialize(inInitialSize);
    }

    /**
     * Creates a new instance of this class.
     * @param inSize
     * @param inIntArray
     * @param inLastIntTable
     * @param inObjectArray
     */
    public IntHashtable(int inSize, int inIntArray[], LastIntTable inLastIntTable, Object inObjectArray[]) {
        setSizeAndArraysTable(inSize, inIntArray, inLastIntTable, inObjectArray);
    }

    @Override
    public void put(int inKey, Object inObject) {
        int arrayIndex = getArrayIndex(inKey);

        if (m_ObjectArray[arrayIndex] == null || m_IntArray[arrayIndex] == inKey) {
            m_IntArray[arrayIndex] = inKey;
            m_ObjectArray[arrayIndex] = inObject;

            return;
        }

        m_DeeperTable.put(inKey, inObject);
    }

    @Override
    public V get(int inKey) {
        int arrayIndex = getArrayIndex(inKey);

        if (m_IntArray[arrayIndex] == inKey) { return (V)m_ObjectArray[arrayIndex]; }

        return (V)m_DeeperTable.get(inKey);
    }

    public Enumeration getEnumerator() {
        return new IntEnumerator(this);
    }

    /**
     * @param inInitialSize
     */
    public void initialize(int inInitialSize) {
        LastIntTable lastResortTable = new LastIntTable();

        int doubleSize = getDoubledSize(inInitialSize);

        setSizeAndArraysTable(doubleSize, new int[doubleSize], lastResortTable, new Object[doubleSize]);

        lastResortTable.setSizeAndArraysTable(inInitialSize, new int[inInitialSize], this, new Object[inInitialSize]);
    }

    @Override
    public V remove(int inKey) {
        if (m_IntArray[getArrayIndex(inKey)] == inKey) {
            super.remove(inKey);
        }

        return (V)m_DeeperTable.remove(inKey);
    }

    @Override
    public void transfer(LastIntTable inLastIntTable) {
        m_DeeperTable.transfer(inLastIntTable);

        super.transfer(inLastIntTable);
    }

    /**
     * An IntEnumerator enumerator class.  This class should remain opaque
     * to the client. It will use the Enumeration interface.
     */
    public class IntEnumerator implements Enumeration {
        final static protected int cNeverNumber = -10;

        protected int              m_Index      = 0;

        protected LastIntTable     m_LastIntTable;

        protected LastIntTable     m_OriginalTable;

        /**
         * Creates a new instance of this class.
         * @param inLastIntTable
         */
        public IntEnumerator(LastIntTable inLastIntTable) {
            m_OriginalTable = inLastIntTable;
            m_LastIntTable = m_OriginalTable;
        }

        @Override
        public boolean hasMoreElements() {
            if (m_Index == cNeverNumber) { return false; }

            while (true) {
                if (m_LastIntTable.m_ObjectArray[m_Index] != null) { return true; }

                m_Index++;

                if (m_Index >= m_LastIntTable.m_ArraySize) {
                    m_LastIntTable = m_LastIntTable.m_DeeperTable;

                    if (m_LastIntTable == m_OriginalTable) {
                        m_Index = cNeverNumber;

                        return false;
                    } else {
                        m_Index = 0;
                    }
                }
            }
        }

        @Override
        public Object nextElement() {
            if (m_Index == cNeverNumber) { return null; }

            while (true) {
                Object outObject = m_LastIntTable.m_ObjectArray[m_Index];

                m_Index++;

                if (outObject != null) {
                    if (m_Index >= m_LastIntTable.m_ArraySize) {
                        m_LastIntTable = m_LastIntTable.m_DeeperTable;

                        if (m_LastIntTable == m_OriginalTable) {
                            m_Index = cNeverNumber;
                        } else {
                            m_Index = 0;
                        }
                    }

                    return outObject;
                }

                if (m_Index >= m_LastIntTable.m_ArraySize) {
                    m_LastIntTable = m_LastIntTable.m_DeeperTable;

                    if (m_LastIntTable == m_OriginalTable) {
                        m_Index = cNeverNumber;

                        return null;
                    } else {
                        m_Index = 0;
                    }
                }
            }
        }

        /**
         * 
         */
        public void reset() {
            m_LastIntTable = m_OriginalTable;
            m_Index = 0;
        }
    }

}
