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
import org.springframework.beans.factory.annotation.Configurable;

import net.proteusframework.core.locale.TextSources;
import net.proteusframework.ui.miwt.component.Container;
import net.proteusframework.ui.miwt.component.Field;
import net.proteusframework.ui.miwt.component.PushButton;
import net.proteusframework.ui.miwt.util.CommonActions;

/**
 * The Detail of specified faculty.
 *
 * @author Yumei Shi (yshi@venturetechasia.net)
 * @since 2/5/15 12:24 AM
 */
@Configurable
public class FacultyDetail extends Container
{
    /** The faculty entity. */
    private Faculty _faculty;

    /**
     * Constructor.
     *
     * @param faculty The faculty.
     */
    public FacultyDetail(Faculty faculty)
    {
        _faculty = faculty;
    }

    @Override
    public void init()
    {
        super.init();
        _setupUI();
    }

    private void _setupUI()
    {
        removeAllComponents();
        addClassName("faculty-detail");

        final PushButton backBtn = CommonActions.BACK.push();
        add(Container.of("back-button", backBtn));
        backBtn.addActionListener(ev -> close());

        Field firstName = new Field(_faculty.getFirstName());
        firstName.setEditable(false);
        Field lastName = new Field(_faculty.getLastName());
        lastName.setEditable(false);
        Field jobGrade = new Field(_faculty.getRankType() + "");
        jobGrade.setEditable(false);
        Field joinDate = new Field(_faculty.getJoinDate().toString());
        joinDate.setEditable(false);
        Field searchArea = new Field(_faculty.getSearchArea());
        searchArea.setEditable(false);
        Field sabbatical = new Field(_faculty.isSabbatical() ? "Yes" : "No");
        sabbatical.setEditable(false);
        add(Container.of("first-name", TextSources.create("first name:"), firstName));
        add(Container.of("last-name", TextSources.create("last name:"), lastName));
        add(Container.of("job", TextSources.create("job grade:"), jobGrade));
        add(Container.of("join-time", TextSources.create("join time:"), joinDate));
        add(Container.of("search-area", TextSources.create("search area:"), searchArea));
        add(Container.of("sabbatical", TextSources.create("on sabbatical:"), sabbatical));
    }
}
