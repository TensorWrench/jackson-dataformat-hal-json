package com.tensorwrench.jackson.hal.models;

import com.tensorwrench.jackson.hal.annotations.HalId;
import com.tensorwrench.jackson.hal.annotations.HalResource;

@HalResource
public class Simple {
	String stringValue;
	double doubleValue;
	@HalId int integerValue;
	
	public String getStringValue()
	{
		return stringValue;
	}
	public void setStringValue(String stringValue)
	{
		this.stringValue = stringValue;
	}
	public double getDoubleValue()
	{
		return doubleValue;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(doubleValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + integerValue;
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
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
		Simple other = (Simple) obj;
		if (Double.doubleToLongBits(doubleValue) != Double.doubleToLongBits(other.doubleValue))
			return false;
		if (integerValue != other.integerValue)
			return false;
		if (stringValue == null) {
			if (other.stringValue != null)
				return false;
		} else if (!stringValue.equals(other.stringValue))
			return false;
		return true;
	}
	public Simple()
	{
	}
	
	public Simple(String stringValue, double doubleValue, int integerValue)
	{
		super();
		this.stringValue = stringValue;
		this.doubleValue = doubleValue;
		this.integerValue = integerValue;
	}
	@Override
	public String toString()
	{
		return "Simple [stringValue=" + stringValue + ", doubleValue=" + doubleValue + ", integerValue=" + integerValue
				+ "]";
	}
	public void setDoubleValue(double doubleValue)
	{
		this.doubleValue = doubleValue;
	}
	public int getIntegerValue()
	{
		return integerValue;
	}
	public void setIntegerValue(int integerValue)
	{
		this.integerValue = integerValue;
	}
	
}
