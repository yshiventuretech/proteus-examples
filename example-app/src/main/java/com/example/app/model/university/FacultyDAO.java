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

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.i2rd.hibernate.AbstractProcessor;

import net.proteusframework.ui.search.QLBuilder;
import net.proteusframework.ui.search.QLBuilderImpl;

/**
 * DAO for faculty.
 *
 * @author Yumei Shi (yshi@venturetechasia.net)
 * @since 1/21/15 2:45 AM
 */
@Repository(FacultyDAO.RESOURCE_NAME)
public class FacultyDAO extends AbstractProcessor<Faculty>
{
    /** Resource name. */
    public static final String RESOURCE_NAME = "com.example.app.model.university.FacultyDAO";

    /**
     * Get all the faculties in the university.
     *
     * @return qb.
     */
    public QLBuilder getAllFacultiesQB()
    {
        return new QLBuilderImpl(Faculty.class,"faculty").setOrderBy("id asc");
    }

    /**
     * Get all the faculties as list.
     *
     * @return the faculties list.
     */
    public List<Faculty> getAllFacultiesAsList()
    {
        return getAllFacultiesQB().getQueryResolver().list();
    }

    /**
     * Save the faculty.
     *
     * @param faculty the faculty to save
     */
    public void saveFaculty(Faculty faculty)
    {
        beginTransaction();
        boolean success = false;
        try
        {
            final Session session = getSession();
            session.saveOrUpdate(faculty);
            success = true;
        }
        finally
        {
            if (success)
                commitTransaction();
            else
                rollbackTransaction();
        }
    }

    /**
     * Delete the specified faculty.
     *
     * @param faculty the faculty to delete.
     */
    public void deleteFaculty(Faculty faculty)
    {
        beginTransaction();
        boolean success = false;
        try
        {
            final Session session = getSession();
            session.delete(faculty);
            success = true;
        }
        finally
        {
            if (success)
                commitTransaction();
            else
                rollbackTransaction();
        }
    }

    @Override
    public Class<Faculty> getEntityType()
    {
        return Faculty.class;
    }
}
