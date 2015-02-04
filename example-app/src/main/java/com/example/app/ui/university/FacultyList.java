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
import net.proteusframework.ui.miwt.component.PushButton;
import net.proteusframework.ui.miwt.component.TextButton;
import net.proteusframework.ui.miwt.component.composite.HistoryContainer;
import net.proteusframework.ui.miwt.event.ActionEvent;
import net.proteusframework.ui.miwt.event.ActionListener;
import net.proteusframework.ui.workspace.Workspace;
import net.proteusframework.ui.workspace.WorkspaceEvent;
import net.proteusframework.ui.workspace.WorkspaceHandler;
import net.proteusframework.ui.workspace.WorkspaceHandlerContext;
import net.proteusframework.ui.workspace.WorkspaceHandlerResult;

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

    private void _setupUI()
    {
        final Container mainCon = new Container();
        mainCon.addClassName("faculty-list");
        setDefaultComponent(mainCon);

        final Label idLabel = new Label(TextSources.create("Id"));
        final Label nameLabel = new Label(TextSources.create("Name"));
        final Label jobGradeLabel = new Label(TextSources.create("Job Grade"));
        mainCon.add(Container.of("column-label", idLabel, nameLabel, jobGradeLabel));

        List<Faculty> facultyList = _facultyDAO.getAllFacultiesAsList();
        for(Faculty faculty : facultyList)
        {
            final Field idField = new Field(faculty.getId() + "");
            idField.setEditable(false);
            final TextButton nameField = new TextButton(TextSources.create(faculty.getFirstName()));
            nameField.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent ev)
                {
                    Dialog dialog = new Dialog(TextSources.create("Dialog Title"));
                    dialog.setVisible(true);
                    dialog.set(20, 20, new Container());
                    getWindowManager().add(dialog);
                    dialog.add(new Label(TextSources.create("Dialog text")));
                    //dialog.close();
                }
            });
            final Field jobGradeField = new Field(faculty.getRankType() + "");
            jobGradeField.setEditable(false);
            mainCon.add(Container.of("content-field", idField, nameField, jobGradeField));
        }
    }
}
