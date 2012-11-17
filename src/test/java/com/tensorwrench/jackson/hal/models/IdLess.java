package com.tensorwrench.jackson.hal.models;

public class IdLess {
	int integer;
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + integer;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdLess other = (IdLess) obj;
		if (integer != other.integer)
			return false;
		return true;
	}
	public IdLess()
	{
	}
	@Override
	public String toString()
	{
		return "IdLess [integer=" + integer + "]";
	}

	public IdLess(int integer)
	{
		super();
		this.integer = integer;
	}

	public int getInteger()
	{
		return integer;
	}

	public void setInteger(int integer)
	{
		this.integer = integer;
	}
	
}
