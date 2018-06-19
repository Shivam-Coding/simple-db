package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples in no particular order. Tuples are
 * stored on pages, each of which is a fixed size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 */
public class HeapFile implements DbFile {

	/**
	 * The File associated with this HeapFile.
	 */
	protected File file;

	/**
	 * The TupleDesc associated with this HeapFile.
	 */
	protected TupleDesc td;

	/**
	 * Constructs a heap file backed by the specified file.
	 * 
	 * @param f
	 *            the file that stores the on-disk backing store for this heap file.
	 */
	public HeapFile(File f, TupleDesc td) {
		this.file = f;
		this.td = td;
	}

	/**
	 * Returns the File backing this HeapFile on disk.
	 * 
	 * @return the File backing this HeapFile on disk.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns an ID uniquely identifying this HeapFile. Implementation note: you will need to generate this tableid
	 * somewhere ensure that each HeapFile has a "unique id," and that you always return the same value for a particular
	 * HeapFile. We suggest hashing the absolute file name of the file underlying the heapfile, i.e.
	 * f.getAbsoluteFile().hashCode().
	 * 
	 * @return an ID uniquely identifying this HeapFile.
	 */
	public int getId() {
		return file.getAbsoluteFile().hashCode();
	}

	/**
	 * Returns the TupleDesc of the table stored in this DbFile.
	 * 
	 * @return TupleDesc of this DbFile.
	 */
	public TupleDesc getTupleDesc() {
		return td;
	}

	// see DbFile.java for javadocs
	public Page readPage(PageId pid) {
		// some code goes here
		int offset = pid.pageno()*BufferPool.PAGE_SIZE;
		byte[] page = new byte[BufferPool.PAGE_SIZE];
		HeapPage hp = null;
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(getFile(), "r");
			raf.seek(offset);
			raf.read(page);
			raf.close();
			hp= new HeapPage((HeapPageId)pid, page);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO: handle exception
		}
		
		return hp;
		//throw new UnsupportedOperationException("Implement this");
	}

	// see DbFile.java for javadocs
	public void writePage(Page page) throws IOException {
		// some code goes here
		// not necessary for assignment1
	}

	/**
	 * Returns the number of pages in this HeapFile.
	 */
	public int numPages() {
		return (int) (file.length() / BufferPool.PAGE_SIZE);
	}

	// see DbFile.java for javadocs
	public ArrayList<Page> addTuple(TransactionId tid, Tuple t) throws DbException, IOException,
			TransactionAbortedException {
		// some code goes here
		return null;
		// not necessary for assignment1
	}

	// see DbFile.java for javadocs
	public Page deleteTuple(TransactionId tid, Tuple t) throws DbException, TransactionAbortedException {
		// some code goes here
		return null;
		// not necessary for assignment1
	}

	// see DbFile.java for javadocs
	public DbFileIterator iterator(TransactionId tid) {
		// some code goes here
		return new irt(tid);
		//throw new UnsupportedOperationException("Implement this");
	}
	
	public class irt implements DbFileIterator{
		
		Iterator<Tuple> it=null;
		BufferPool bp ;
		HeapPage hp;
		boolean b;
		TransactionId tid;
		int i ,j=0;
		public irt(TransactionId tid){
			this.tid=tid;
			bp= Database.getBufferPool();
			i=numPages();
			HeapPageId pid = new HeapPageId(getId(), j);
			try {
				hp=(HeapPage) bp.getPage(tid,pid , null);
			} catch (TransactionAbortedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void open() throws DbException, TransactionAbortedException {
			// TODO Auto-generated method stub
			
			it  = hp.iterator();
		}

		@Override
		public boolean hasNext() throws DbException,
				TransactionAbortedException {
			// TODO Auto-generated method stub
			if(it==null){
				//throw new DbException("not open");
				return false;
			}
			b = it.hasNext();
			if(b)
			{
//				System.out.println(hp.tuples.size());
				return true;
				
			}
			else{
				j++;
				if(j<numPages()){
					HeapPageId pid = new HeapPageId(getId(), j);
					try {
						hp=(HeapPage) bp.getPage(tid,pid , null);
					} catch (TransactionAbortedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					open();
				}else{
					return false;
				}
			}
			return hasNext();
		
		}

		@Override
		public Tuple next() throws DbException, TransactionAbortedException,
				NoSuchElementException {
			// TODO Auto-generated method stub
			if(it==null){
				throw new NoSuchElementException();
			}
			if(b){
				return (Tuple)it.next();
			}
			else{
				j++;
				if(j<numPages()){
					HeapPageId pid = new HeapPageId(getId(), j);
					try {
						hp=(HeapPage) bp.getPage(tid,pid , null);
					} catch (TransactionAbortedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					open();
					hasNext();
				}else{
					return null;
				}
			}
			return (Tuple)it.next();
		}

		@Override
		public void rewind() throws DbException, TransactionAbortedException {
			// TODO Auto-generated method stub
			it=hp.iterator();
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
			it = null;
			
		}
		
	}

}
