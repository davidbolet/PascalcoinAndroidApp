package org.pascalcoin.pascalcoinofficial.model;

import java.util.Date;


public abstract class BaseEntity {

    private int id;

    private Date createdAt;

	private Date updatedAt;
	
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
    @Override
    public String toString() {
        return String.format("%s(id=%d, created_at=%s, updated_at=%s)",
        		this.getClass().getSimpleName(), 
        		this.getId(),
        		this.getCreatedAt(),
        		this.getUpdatedAt());
    }
    

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;

        if (o instanceof BaseEntity) {
            final BaseEntity other = (BaseEntity) o;
            return (getId()==other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return String.valueOf(31*this.getCreatedAt().getTime()*getId()).hashCode();
    }

}
