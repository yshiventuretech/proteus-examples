/*
 * Copyright (c) Interactive Information R & D (I2RD) LLC.
 * All Rights Reserved.
 *
 * This software is confidential and proprietary information of
 * I2RD LLC ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered
 * into with I2RD.
 */

package com.example.app.model.university;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import java.sql.Date;

import net.proteusframework.core.hibernate.model.AbstractEntity;

/**
 * Faculty of the university.
 * It includes the base information of the professor.
 *
 * @author Yumei Shi (yshi@venturetechasia.net)
 * @since 1/16/15 9:26 PM
 */
@Entity
public class Faculty extends AbstractEntity
{
	private String _firstName;
	private String _lastName;
	/** The slug   */
	private String _slug;
	/**  */
	private RankType _rankType;
	/**  */
	private Date _joinDate;
	/**  */
	private String _searchArea;
	/**  */
	private boolean _sabbatical = false;

	/**
	 * Get the faculty id.
	 *
	 * @return id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "faculty_id_seq")
	@SequenceGenerator(name = "faculty_id_seq", sequenceName = "faculty_id_seq")
	@Override
	public Long getId()
	{
		return super.getId();
	}

	@NotNull
	public String getFirstName()
	{
		return _firstName;
	}

	public void setFirstName(String firstName)
	{
		_firstName = firstName;
	}

	@NotNull
	public String getLastName()
	{
		return _lastName;
	}

	public void setLastName(String lastName)
	{
		_lastName = lastName;
	}

	@Column(unique = true,nullable = false)
	public String getSlug()
	{
		return _slug;
	}

	public void setSlug(String slug)
	{
		_slug = slug;
	}

	/**
	 * Get the RankType
	 *
	 * @return the rankType
	 */
	@Enumerated(EnumType.STRING)
	public RankType getRankType()
	{
		return _rankType;
	}

	/**
	 * Set the RankType
	 *
	 * @param rankType
	 */
	public void setRankType(RankType rankType)
	{
		_rankType = rankType;
	}
	/**
	 * Get the joinDate
	 *
	 * @return joinDate
	 */
	public Date getJoinDate()
	{
		return _joinDate;
	}

	/**
	 * Set the joinDate
	 *
	 * @param joinDate
	 */
	public void setJoinDate(Date joinDate)
	{
		_joinDate = joinDate;
	}

	public String getSearchArea()
	{
		return _searchArea;
	}
	public void setSearchArea(String searchArea)
	{
		_searchArea = searchArea;
	}

	@Column(nullable = false, columnDefinition = "boolean default false")
	public boolean isSabbatical()
	{
		return _sabbatical;
	}
	public void setSabbatical(boolean sabbatical)
	{
		_sabbatical = sabbatical;
	}

}
