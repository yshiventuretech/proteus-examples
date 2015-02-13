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

package com.example.app.ui.university;

import com.example.app.model.university.Faculty;
import com.example.app.model.university.FacultyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;

import java.util.List;

import com.i2rd.cms.bean.MIWTBeanConfig;
import com.i2rd.cms.bean.MIWTStateEvent;
import com.i2rd.cms.miwt.SiteAwareMIWTApplication;

import net.proteusframework.core.locale.TextSources;
import net.proteusframework.ui.miwt.component.Container;
import net.proteusframework.ui.miwt.component.Dialog;
import net.proteusframework.ui.miwt.component.Field;
import net.proteusframework.ui.miwt.component.Label;
import net.proteusframework.ui.miwt.component.TextButton;
import net.proteusframework.ui.miwt.component.composite.HistoryContainer;

/**
 * List all the faculties.
 *
 * @author Yumei Shi (yshi@venturetechasia.net)
 * @since 1/29/15 3:13 AM
 */
@MIWTBeanConfig(value = FacultyList.RESOURCE_NAME, displayName = "Faculty list",
    applicationClass = SiteAwareMIWTApplication.class, stateEvents = {
    @MIWTStateEvent(eventName = SiteAwareMIWTApplication.SAMA_ADD_COMPONENT, eventValue = FacultyList.RESOURCE_NAME ),
    @MIWTStateEvent(eventName = SiteAwareMIWTApplication.SAMA_RECREATE_ON_SITE_CHANGE, eventValue = "true")})
@Scope("prototype")
@Configurable
public class FacultyList extends HistoryContainer
{
    /** The resource name. */
    final static String RESOURCE_NAME = "com.example.app.ui.university.FacultyList";
    /* The faculty DAO. **/
    @Autowired
    private FacultyDAO _facultyDAO;

    @Override
    public void init()
    {
        super.init();
        _setupUI();
    }

    /**
     * Set up the components.
     */
    private void _setupUI()
    {
        final Container mainCon = new Container();
        mainCon.addClassName("faculty-list");
        setDefaultComponent(mainCon);

        List<Faculty> facultyList = _facultyDAO.getAllFacultiesAsList();
        for(Faculty faculty : facultyList)
        {
            final TextButton nameField = new TextButton(TextSources.create(faculty.getFirstName()));
            nameField.addActionListener(ev -> {
                String slug = faculty.getSlug();
                final Dialog dialog = new Dialog(TextSources.create(slug));
                dialog.setVisible(true);
                getWindowManager().add(dialog);
                Container container = new Container();
                container.addClassName("faculty-dialog");
                _setup(container, faculty);
                dialog.add(container);
            });
            Container nameFieldCon = Container.of("name-field", nameField);
            final Label jobGradeField = new Label(TextSources.create(faculty.getRankType() + ""));
            Container jobFieldCon = Container.of("job-field", jobGradeField);
            final Label areaField = new Label(TextSources.create(faculty.getSearchArea()));
            Container areaFieldCon = Container.of("area-field", areaField);
            mainCon.add(Container.of("content-field", nameFieldCon, jobFieldCon, areaFieldCon));
        }
    }

    /**
     * Set up the dialog container which contains the faculty entity.
     * @param container The container.
     * @param faculty The faculty entity.
     */
    private void _setup(Container container, Faculty faculty)
    {
        Field firstName = new Field(faculty.getFirstName());
        firstName.setEditable(false);
        Field lastName = new Field(faculty.getLastName());
        lastName.setEditable(false);
        Field jobGrade = new Field(faculty.getRankType() + "");
        jobGrade.setEditable(false);
        Field joinDate = new Field(faculty.getJoinDate().toString());
        joinDate.setEditable(false);
        Field searchArea = new Field(faculty.getSearchArea());
        searchArea.setEditable(false);
        Field sabbatical = new Field(faculty.isSabbatical() ? "Yes" : "No");
        sabbatical.setEditable(false);
        container.add(Container.of("first-name", TextSources.create("First Name:"), firstName));
        container.add(Container.of("last-name", TextSources.create("Last Name:"), lastName));
        container.add(Container.of("job", TextSources.create("Job Grade:"), jobGrade));
        container.add(Container.of("join-time", TextSources.create("Join Time:"), joinDate));
        container.add(Container.of("search-area", TextSources.create("Search Area:"), searchArea));
        container.add(Container.of("sabbatical", TextSources.create("On Sabbatical:"), sabbatical));
    }
}
