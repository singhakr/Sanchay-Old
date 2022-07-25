/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.common;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 *
 * @author Anil Kumar Singh
 */
public class HypothesisStack<E> extends LinkedList<E>
        implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{

    private transient Entry<E> header = new Entry<E>(null, null, null);
    private transient int size = 0;

    private static class Entry<E>
    {

        E element;
        Entry<E> next;
        Entry<E> previous;

        Entry(E element, Entry<E> next, Entry<E> previous)
        {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }

    private Entry<E> addBefore(E e, Entry<E> entry)
    {
        Entry<E> newEntry = new Entry<E>(e, entry, entry.previous);
        newEntry.previous.next = newEntry;
        newEntry.next.previous = newEntry;
        size++;
        modCount++;
        return newEntry;
    }

    private E remove(Entry<E> e)
    {
        if (e == header)
        {
            throw new NoSuchElementException();
        }

        E result = e.element;
        e.previous.next = e.next;
        e.next.previous = e.previous;
        e.next = e.previous = null;
        e.element = null;
        size--;
        modCount++;
        return result;
    }

    private class ListItr implements ListIterator<E>
    {

        private Entry<E> lastReturned = header;
        private Entry<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index)
        {
            if (index < 0 || index > size)
            {
                throw new IndexOutOfBoundsException("Index: " + index +
                        ", Size: " + size);
            }
            if (index < (size >> 1))
            {
                next = header.next;
                for (nextIndex = 0; nextIndex < index; nextIndex++)
                {
                    next = next.next;
                }
            } else
            {
                next = header;
                for (nextIndex = size; nextIndex > index; nextIndex--)
                {
                    next = next.previous;
                }
            }
        }

        public boolean hasNext()
        {
            return nextIndex != size;
        }

        public E next()
        {
            checkForComodification();
            if (nextIndex == size)
            {
                throw new NoSuchElementException();
            }

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.element;
        }

        public boolean hasPrevious()
        {
            return nextIndex != 0;
        }

        public E previous()
        {
            if (nextIndex == 0)
            {
                throw new NoSuchElementException();
            }

            lastReturned = next = next.previous;
            nextIndex--;
            checkForComodification();
            return lastReturned.element;
        }

        public int nextIndex()
        {
            return nextIndex;
        }

        public int previousIndex()
        {
            return nextIndex - 1;
        }

        public void remove()
        {
            checkForComodification();
            Entry<E> lastNext = lastReturned.next;
            try
            {
                HypothesisStack.this.remove(lastReturned);
            } catch (NoSuchElementException e)
            {
                throw new IllegalStateException();
            }
            if (next == lastReturned)
            {
                next = lastNext;
            } else
            {
                nextIndex--;
            }
            lastReturned = header;
            expectedModCount++;
        }

        public void set(E e)
        {
            if (lastReturned == header)
            {
                throw new IllegalStateException();
            }
            checkForComodification();
            lastReturned.element = e;
        }

        public void add(E e)
        {
            checkForComodification();
            lastReturned = header;
            addBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        final void checkForComodification()
        {
            if (modCount != expectedModCount)
            {
                throw new ConcurrentModificationException();
            }
        }
    }

    /** Adapter to provide descending iterators via ListItr.previous */
    private class DescendingIterator implements Iterator
    {

        final ListItr itr = new ListItr(size());

        public boolean hasNext()
        {
            return itr.hasPrevious();
        }

        public E next()
        {
            return itr.previous();
        }

        public void remove()
        {
            itr.remove();
        }
    }

    public Object clone()
    {
        HypothesisStack<E> clone = null;
//        try
//        {s
            clone = (HypothesisStack<E>) super.clone();
//        } catch (CloneNotSupportedException e)
//        {
//            throw new InternalError();
//        }

        // Put clone into "virgin" state
        clone.header = new Entry<E>(null, null, null);
        clone.header.next = clone.header.previous = clone.header;
        clone.size = 0;
        clone.modCount = 0;

        // Initialize clone with our elements
        for (Entry<E> e = header.next; e != header; e = e.next)
        {
            clone.add(e.element);
        }

        return clone;
    }

    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException
    {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Entry e = header.next; e != header; e = e.next)
        {
            s.writeObject(e.element);
        }
    }

    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException
    {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Initialize header
        header = new Entry<E>(null, null, null);
        header.next = header.previous = header;

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
        {
            addBefore((E) s.readObject(), header);
        }
    }
}
