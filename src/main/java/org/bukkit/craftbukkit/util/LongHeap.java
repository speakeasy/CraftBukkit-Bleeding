package org.bukkit.craftbukkit.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LongHeap {
    long elements[] = new long[1];

    private void swap(int i, int j) {
        long temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }

    public synchronized int insert(long value) {
        elements[0]++;
        if (elements[0] == elements.length) {
            long tempElements[] = new long[elements.length * 2];
            System.arraycopy(elements, 0, tempElements, 0, elements.length);
            elements = tempElements;
        }
        elements[(int) elements[0]] = value;
        return bubbleUp();
    }

    private int bubbleUp() {
        int index = (int) elements[0];
        int parent;
        while (index > 1) {
            parent = index >> 1;
            if (elements[index] < elements[parent]) {
                swap(index, parent);
                index = parent;
            } else
                break; // if we don't swap, we're done
        }
        return index;
    }

    public long peekHead() {
        return elements[1];
    }

    public long popHead() {
        long value = peekHead();
        removeHead();
        return value;
    }

    public synchronized void removeHead() {
        remove(1);
    }

    public synchronized void remove(int index) {
        if (index < 1 || index > elements[0])
            return;
        if (elements[0]-- != index) {
            swap(index, (int) (elements[0] + 1));
            bubbleDown(index);
        }
    }

    private void bubbleDown(int index) {
        int count = (int) elements[0];
        while (true) {
            int left = index << 1;
            if (left == count) { // left child is last element
                if (elements[left] < elements[index])
                    swap(index, left); // if child is smaller, swap
                return; // we have no more children so stop
            } else if (left < count) { // we have at least a right child
                int minChild = elements[left] < elements[left + 1] ? left : left + 1; // find minimum child
                if (elements[minChild] < elements[index]) { // and find minumum compared to parent
                    swap(index, minChild);
                    index = minChild;
                } else
                    break; // we're finished if we didn't need to swap
            } else
                break;
        }
    }

    public boolean isEmpty() {
        return elements[0] == 0;
    }

    public void removeByCoord(int coord) {
        coord = coord << 16;
        for (int i = 1; i <= elements[0]; i++) {
            if ((int) (elements[i] & 0xFFFF0000) == coord) {
                remove(i);
                return;
            }
        }
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            for (int i = 1; i <= elements[0]; i++) {
                dos.writeLong(elements[i]);
            }
        } catch (IOException e) {
            // yes, out of memory, ./care
        } finally {
            try {
                dos.close();
                bos.close();
            } catch (IOException e) {
            }
        }
        return bos.toByteArray();
    }
}
