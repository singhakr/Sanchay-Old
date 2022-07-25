package sanchay.speech.decoder.connected;

import sanchay.speech.common.*;

public class TrellisPos {
	private int colind;
	private int rowind;
	private int mdlsize;

	public TrellisPos()
	{
		colind = -1;
		rowind = -1;
		mdlsize = -1;
	}

	public int getColIndex()
	{
		return colind;
	}

	public void setColIndex(int c)
	{
		colind = c;
	}

	public int getRowIndex()
	{
		return rowind;
	}

	public void setRowIndex(int c)
	{
		rowind = c;
	}

	public int getModelSize()
	{
		return mdlsize;
	}

	public void setModelSize(int s)
	{
		mdlsize = s;
	}
}
