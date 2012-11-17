package com.tensorwrench.jackson.hal.models;

import com.tensorwrench.jackson.hal.annotations.HalId;
import com.tensorwrench.jackson.hal.annotations.HalLink;
import com.tensorwrench.jackson.hal.annotations.HalResource;

@HalResource
public class LinksSimple {
	public LinksSimple()
	{
	}
	public LinksSimple(int id, Simple simple)
	{
		super();
		this.id = id;
		this.simple = simple;
	}
	@HalId int id;
	@HalLink Simple simple;
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public Simple getSimple()
	{
		return simple;
	}
	public void setSimple(Simple simple)
	{
		this.simple = simple;
	}
	@Override
	public String toString()
	{
		return "LinksSimple [id=" + id + ", simple=" + simple + "]";
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((simple == null) ? 0 : simple.hashCode());
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
		LinksSimple other = (LinksSimple) obj;
		if (id != other.id)
			return false;
		if (simple == null) {
			if (other.simple != null)
				return false;
		} else if (!simple.equals(other.simple))
			return false;
		return true;
	}
	
	
}
