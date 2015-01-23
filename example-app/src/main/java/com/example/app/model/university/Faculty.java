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
    /** Sequence name. */
    private final static String SEQ = "faculty_id_seq";
    /** The first name. */
    private String _firstName;
    /** The last name. */
    private String _lastName;
    /** The unique slug. */
    private String _slug;
    /** The faculty rank type. */
    private RankType _rankType;
    /** The faculty join date. */
    private Date _joinDate;
    /** The search are of the faculty. */
    private String _searchArea;
    /** The faculty if on sabbatical. */
    private boolean _sabbatical = false;

    /**
     * Get the identifier.
     *
     * @return the identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = SEQ)
    @SequenceGenerator(name = SEQ, sequenceName = SEQ)
    @Override
    public Long getId()
    {
        return super.getId();
    }

    /**
     * Get the first name.
     *
     * @return the first name.
     */
    @NotNull
    public String getFirstName()
    {
        return _firstName;
    }

    /**
     * Set the first name.
     *
     * @param firstName the first name.
     */
    public void setFirstName(String firstName)
    {
        _firstName = firstName;
    }

    /**
     * Get the last name.
     *
     * @return the last name.
     */
    @NotNull
    public String getLastName()
    {
        return _lastName;
    }

    /**
     * Set the last name.
     *
     * @param lastName the last name.
     */
    public void setLastName(String lastName)
    {
        _lastName = lastName;
    }



    /**
     * Get the unique slug.
     *
     * @return the slug.
     */
    @Column(unique = true,nullable = false)
    public String getSlug()
    {
        return _slug;
    }

    /**
     * Set the slug.
     *
     * @param slug the slug.
     */
    public void setSlug(String slug)
    {
        _slug = slug;
    }

    /**
     * Get the rank type.
     *
     * @return the rank type.
     */
    @Enumerated(EnumType.STRING)
    public RankType getRankType()
    {
        return _rankType;
    }

    /**
     * Set the rank type.
     *
     * @param rankType the rank type.
     */
    public void setRankType(RankType rankType)
    {
        _rankType = rankType;
    }

    /**
     * Get the join date.
     *
     * @return the join date.
     */
    public Date getJoinDate()
    {
        return _joinDate;
    }

    /**
     * Set the join date.
     *
     * @param joinDate the join date.
     */
    public void setJoinDate(Date joinDate)
    {
        _joinDate = joinDate;
    }

    /**
     * Get the search area.
     *
     * @return the search area.
     */
    public String getSearchArea()
    {
        return _searchArea;
    }

    /**
     * Set the search area.
     *
     * @param searchArea the search area.
     */
    public void setSearchArea(String searchArea)
    {
        _searchArea = searchArea;
    }

    /**
     * Get the sabbatical.
     *
     * @return the sabbatical.
     */
    @Column(nullable = false, columnDefinition = "boolean default false")
    public boolean isSabbatical()
    {
        return _sabbatical;
    }

    /**
     * Set the sabbatical.
     *
     * @param sabbatical the sabbatical.
     */
    public void setSabbatical(boolean sabbatical)
    {
        _sabbatical = sabbatical;
    }
}

/**
 * RankType for the faculty.
 */
enum RankType
{
    /** The Lecturer. */
    Lecturer,
    /** The Adjunct Professor. */
    AdjunctProfessor,
    /** The Assistant Professor. */
    AssistantProfessor,
    /** The Associate Profess. */
    AssociateProfessor,
    /** The Professor. */
    Professor;
}
