/*
 * Created on 4/Jan/2006
 *
 */
package pt.utl.ist.repox.util.structure;

class LastIntTable<V> {
	public final static int cNeverNumber = -1234567899;

	public int m_ArraySize;

	public int m_IntArray[];

	public LastIntTable<V> m_DeeperTable;

	public Object m_ObjectArray[];

	public LastIntTable() {
	}

	public void put(int inKey, V inObject) {
		int arrayIndex = getArrayIndex(inKey);

		if (m_ObjectArray[arrayIndex] == null
				|| m_IntArray[arrayIndex] == inKey) {
			m_IntArray[arrayIndex] = inKey;
			m_ObjectArray[arrayIndex] = inObject;

			return;
		}

		//	 create new IntHashtable and attach it to outer IntHashtable

		IntHashtable intHashMap = new IntHashtable(
				m_DeeperTable.m_ArraySize, m_DeeperTable.m_IntArray,
				m_DeeperTable.m_DeeperTable, m_DeeperTable.m_ObjectArray);

		int doubledSize = getDoubledSize(m_DeeperTable.m_ArraySize);

		m_DeeperTable
				.setSizeAndArraysTable(doubledSize, new int[doubledSize],
						intHashMap, new Object[doubledSize]);

		//	 transfer

		m_DeeperTable.put(inKey, inObject);
		intHashMap.transfer(m_DeeperTable);
	}

	public V get(int inKey) {
		int arrayIndex = getArrayIndex(inKey);

		if (m_IntArray[arrayIndex] == inKey) {
			return (V)m_ObjectArray[arrayIndex];
		}

		return null;
	}

	public int getArrayIndex(int inKey) {
		return (inKey & 0x7FFFFFFF) % m_ArraySize;
	}

	public int getDoubledSize(int inSize) {
		return 2 * inSize + 1;
	}

	public V remove(int inKey) {
		int arrayIndex = getArrayIndex(inKey);

		Object outObject = m_ObjectArray[arrayIndex];

		m_IntArray[arrayIndex] = cNeverNumber;
		m_ObjectArray[arrayIndex] = null;

		return (V)outObject;
	}

	public void setSizeAndArraysTable(int inSize, int inLongArray[],
			LastIntTable inLastIntTable, Object inObjectArray[]) {
		m_ArraySize = inSize;

		m_IntArray = inLongArray;
		m_ObjectArray = inObjectArray;

		m_DeeperTable = inLastIntTable;
	}

	public void transfer(LastIntTable inLastIntTable) {
		for (int index = 0; index < m_ArraySize; index++) {
			if (m_ObjectArray[index] != null) {
				int oldInt = m_IntArray[index];

				Object oldObject = m_ObjectArray[index];

				m_IntArray[index] = cNeverNumber;
				m_ObjectArray[index] = null;

				inLastIntTable.put(oldInt, oldObject);
			}
		}
	}
}
