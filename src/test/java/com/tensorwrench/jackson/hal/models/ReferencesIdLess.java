package com.tensorwrench.jackson.hal.models;

import com.tensorwrench.jackson.hal.annotations.HalLink;
import com.tensorwrench.jackson.hal.annotations.HalResource;

@HalResource
public class ReferencesIdLess {
	@HalLink IdLess idLess;
	String name;
	
	public ReferencesIdLess()	{	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idLess == null) ? 0 : idLess.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ReferencesIdLess other = (ReferencesIdLess) obj;
		if (idLess == null) {
			if (other.idLess != null)
				return false;
		} else if (!idLess.equals(other.idLess))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "ReferencesIdLess [idLess=" + idLess + ", name=" + name + "]";
	}

	public ReferencesIdLess(IdLess idLess, String name)
	{
		this.idLess = idLess;
		this.name = name;
	}

	public IdLess getIdLess()
	{
		return idLess;
	}
	public void setIdLess(IdLess idLess)
	{
		this.idLess = idLess;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
}
