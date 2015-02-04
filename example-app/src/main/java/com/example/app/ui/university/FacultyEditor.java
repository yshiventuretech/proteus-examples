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
import com.example.app.model.university.RankType;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import net.proteusframework.core.hibernate.dao.EntityRetriever;
import net.proteusframework.core.locale.TextSources;
import net.proteusframework.ui.miwt.ButtonGroup;
import net.proteusframework.ui.miwt.component.Calendar;
import net.proteusframework.ui.miwt.component.ComboBox;
import net.proteusframework.ui.miwt.component.Container;
import net.proteusframework.ui.miwt.component.PushButton;
import net.proteusframework.ui.miwt.component.RadioButton;
import net.proteusframework.ui.miwt.component.composite.HistoryContainer;
import net.proteusframework.ui.miwt.component.composite.MessageContainer;
import net.proteusframework.ui.miwt.component.Field;
import net.proteusframework.ui.miwt.data.SimpleListModel;
import net.proteusframework.ui.miwt.event.ActionEvent;
import net.proteusframework.ui.miwt.event.ActionListener;
import net.proteusframework.ui.miwt.util.CommonActions;

/**
 * Editor for add the new faculty entity.
 *
 * @author Yumei Shi (yshi@venturetechasia.net)
 * @since 1/29/15 12:10 AM
 */
@Configurable
public class FacultyEditor extends HistoryContainer
{
	/** Logger. */
	private final static Logger _logger = Logger.getLogger(FacultyEditor.class);
	/** The first name. */
	private Field _firstName;
	/** The last name. */
	private Field _lastName;
	/** The job grade. */
	private ComboBox _jobGrade;
	/** The join date. */
	private Calendar _joinDate;
	/** The search area. */
	private Field _searchArea;
	/** The Yes radio. */
	private RadioButton _yesRadio;
	/** The faculty entity. */
	private Faculty _faculty;
	/** The faculty DAO. */
	@Autowired
	private FacultyDAO _facultyDAO;

	/**
	 * Constructor.
	 */
	public FacultyEditor()
	{
		_faculty = new Faculty();
	}

	/**
	 * Constructor.
	 * @param faculty The faculty.
	 */
	public FacultyEditor(@NotNull Faculty faculty)
	{
		this._faculty = faculty;
	}

	@Override
	public void init()
	{
		super.init();
		_setupUI();
	}

	private void _setupUI()
	{
		final Container mainCon = new Container();
		mainCon.addClassName("faculty-editor");
		setDefaultComponent(mainCon);

		//TODO: use the msg.
		/* The message container. */
		MessageContainer msg = new MessageContainer(TimeUnit.SECONDS.toMillis(60));
		mainCon.add(Container.of("message-container", msg));
		_firstName = new Field(_faculty.getFirstName(), 20, 1);
		_lastName = new Field(_faculty.getLastName(), 20, 1);
		SimpleListModel<RankType> rankList = new SimpleListModel<>();
		rankList.addAll(Arrays.asList(RankType.values()));
		_jobGrade = new ComboBox(rankList);
		_jobGrade.setSelectedObject(_faculty.getRankType());

		_joinDate = new Calendar();
		if(null != _faculty.getJoinDate())
		{
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(_faculty.getJoinDate());
			_joinDate.setCalendar(calendar);
		}

		_searchArea = new Field(_faculty.getSearchArea(), 40, 4);
		/* The radio group. */
		ButtonGroup buttonGroup = new ButtonGroup();
		_yesRadio = new RadioButton(TextSources.create("Yes"), buttonGroup);
		/* The No radio. */
		RadioButton noRadio = new RadioButton(TextSources.create("No"), buttonGroup);
		if(_faculty.isSabbatical())
			_yesRadio.setSelected(true);
		else
			noRadio.setSelected(true);

		PushButton submitBtn=CommonActions.SUBMIT.push();
		submitBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				_saveFaculty();
				close();
			}
		});

		PushButton cancelBtn= CommonActions.CANCEL.push();
		cancelBtn.addActionListener(ev -> close());

		mainCon.add(Container.of("firstname", TextSources.create("first name:"), _firstName));
		mainCon.add(Container.of("lastname", TextSources.create("last name:"), _lastName));
		mainCon.add(Container.of("job", TextSources.create("job grade:"), _jobGrade));
		mainCon.add(Container.of("jointime", TextSources.create("join time:"), _joinDate));
		mainCon.add(Container.of("searcharea", TextSources.create("search area:"), _searchArea));
		mainCon.add(Container.of("sabbatical", TextSources.create("on sabbatical:"), _yesRadio, noRadio));
		mainCon.add(Container.of("pushbutton", submitBtn, cancelBtn));
	}

	private void _saveFaculty()
	{
		String firstStr = _firstName.getText();
		String lastStr = _lastName.getText();
		String slugStr = (firstStr.charAt(0) + lastStr).toLowerCase();
		_faculty = EntityRetriever.getInstance().reattachIfNecessary(_faculty);
		if(!firstStr.equals(_faculty.getFirstName()) || !lastStr.equals(_faculty.getLastName()))
		{
			_faculty.setFirstName(firstStr);
			_faculty.setLastName(lastStr);
			_faculty.setSlug(slugStr);
		}
		_faculty.setRankType((RankType)_jobGrade.getSelectedObject());
		Date joinDate;
		try{
			joinDate = new Date(_joinDate.getDate().getTime());
			_faculty.setJoinDate(joinDate);
		}
		catch(NullPointerException e)
		{
			_logger.error("no date", e);
		}

		if(!_searchArea.getText().equals(_faculty.getSearchArea()))
			_faculty.setSearchArea(_searchArea.getText());
		_faculty.setSabbatical(_yesRadio.isSelected());
		try
		{
			_facultyDAO.saveFaculty(_faculty);
		}
		catch(Exception e)
		{
			_logger.info("Save fail", e);
		}
	}
}
