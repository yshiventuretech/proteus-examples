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
import com.google.common.base.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;

import javax.validation.constraints.NotNull;
import javax.xml.soap.Text;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.i2rd.cms.bean.MIWTBeanConfig;
import com.i2rd.cms.bean.MIWTStateEvent;
import com.i2rd.cms.miwt.BlankColumn;
import com.i2rd.cms.miwt.SiteAwareMIWTApplication;

import net.proteusframework.core.hibernate.dao.EntityRetriever;
import net.proteusframework.core.html.HTMLElement;
import net.proteusframework.core.locale.LocaleContext;
import net.proteusframework.core.locale.LocalizedText;
import net.proteusframework.core.locale.TextSource;
import net.proteusframework.core.locale.TextSources;
import net.proteusframework.core.locale.annotation.I18N;
import net.proteusframework.core.locale.annotation.I18NFile;
import net.proteusframework.core.locale.annotation.L10N;
import net.proteusframework.ui.column.PropertyColumn;
import net.proteusframework.ui.miwt.component.CardContainer;
import net.proteusframework.ui.miwt.component.Component;
import net.proteusframework.ui.miwt.component.Container;
import net.proteusframework.ui.miwt.component.PushButton;
import net.proteusframework.ui.miwt.component.composite.HistoryContainer;
import net.proteusframework.ui.miwt.component.Label;
import net.proteusframework.ui.miwt.component.composite.MessageContainer;
import net.proteusframework.ui.miwt.component.event.ComponentAdapter;
import net.proteusframework.ui.miwt.component.event.ComponentEvent;
import net.proteusframework.ui.miwt.event.ActionEvent;
import net.proteusframework.ui.miwt.event.ActionListener;
import net.proteusframework.ui.miwt.util.CommonButtonText;
import net.proteusframework.ui.search.ActionColumn;
import net.proteusframework.ui.search.ComboBoxConstraint;
import net.proteusframework.ui.search.PropertyConstraint;
import net.proteusframework.ui.search.QLBuilder;
import net.proteusframework.ui.search.QLOrderByImpl;
import net.proteusframework.ui.search.SearchModelImpl;
import net.proteusframework.ui.search.SearchResultColumnImpl;
import net.proteusframework.ui.search.SearchSupplierImpl;
import net.proteusframework.ui.search.SearchUIAction;
import net.proteusframework.ui.search.SearchUIImpl;
import net.proteusframework.ui.search.SearchUIOperation;
import net.proteusframework.ui.search.SearchUIOperationContext;
import net.proteusframework.ui.search.SearchUIOperationHandler;
import net.proteusframework.ui.search.SimpleConstraint;
import net.proteusframework.ui.workspace.AbstractUITask;
import net.proteusframework.ui.workspace.EntityWorkspaceEvent;
import net.proteusframework.ui.workspace.ListTaskManager;
import net.proteusframework.ui.workspace.StandardEventType;
import net.proteusframework.ui.workspace.WorkspaceEvent;
import net.proteusframework.ui.workspace.WorkspaceHandler;
import net.proteusframework.ui.workspace.WorkspaceHandlerContext;
import net.proteusframework.ui.workspace.WorkspaceHandlerResult;
import net.proteusframework.ui.workspace.WorkspaceImpl;

/**
 * Manage the university's faculties.
 *
 * @author Yumei Shi (yshi@venturetechasia.net)
 * @since 1/28/15 2:09 AM
 */
@I18NFile(symbolPrefix = FacultyManagement.RESOURCE_NAME, i18n = {
	@I18N(symbol = "add_faculty", l10n = @L10N("Add Faculty")),
	@I18N(symbol = "search_faculty", l10n = @L10N("University Faculty"))
})
@MIWTBeanConfig(value = FacultyManagement.RESOURCE_NAME, displayName = "Faculty Management",
		applicationClass = SiteAwareMIWTApplication.class, stateEvents = {
		@MIWTStateEvent(eventName = SiteAwareMIWTApplication.SAMA_ADD_COMPONENT, eventValue = FacultyManagement.RESOURCE_NAME),
		@MIWTStateEvent(eventName = SiteAwareMIWTApplication.SAMA_RECREATE_ON_SITE_CHANGE, eventValue = "true")})
@Scope("prototype")
@Configurable
public class FacultyManagement extends HistoryContainer implements SearchUIOperationHandler, WorkspaceHandler
{
	/** The resource name. */
	final static String RESOURCE_NAME = "com.example.app.ui.university.FacultyManagement";
	/** The search ui. */
	private SearchUIImpl _searchUI;
	/** The workspace. */
	private WorkspaceImpl _workspace;
	/** The faculty DAO. */
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
		mainCon.addClassName("faculty-management");
		setDefaultComponent(mainCon);
		/** The add button. */
		final PushButton addBtn = new PushButton(FacultyManagementLOK.ADD_FACULTY());
		mainCon.add(Container.of("add-faculty", addBtn));
		addBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				_workspace.handle(new EntityWorkspaceEvent<>(this, new Faculty(), StandardEventType.creation));
			}
		});

		/** The workspace. */
		final MessageContainer notifiable = new MessageContainer(TimeUnit.SECONDS.toMillis(60));
		CardContainer taskContainer = new CardContainer();
		_workspace  = new WorkspaceImpl(new ListTaskManager(), notifiable, taskContainer);
		mainCon.add(_workspace.getUITaskManager().getComponent());
		mainCon.add(taskContainer);
		_workspace.registerHandler(this);

		/** Constraint */
		/* The first name constraint */
		final SimpleConstraint nameCons = new SimpleConstraint();
		nameCons.setHTMLClass("name");
		nameCons.setLabel(new LocalizedText("FirstName:"));
		nameCons.setOperator(PropertyConstraint.Operator.like);
		nameCons.setProperty("firstName");
		/* The rank type constraint */
		List<RankType> rankList = new ArrayList<RankType>();
		rankList.addAll(Arrays.asList(RankType.values()));
		final ComboBoxConstraint rankCons = new ComboBoxConstraint(rankList, CommonButtonText.ANY, CommonButtonText.ANY);
		rankCons.setHTMLClass("rank");
		rankCons.setLabel(new LocalizedText("JobGrade:"));
		rankCons.setOperator(PropertyConstraint.Operator.eq);
		rankCons.setProperty("rankType");

		/* The action column */
		final ActionColumn actionColumn = new ActionColumn();
		final BlankColumn blankColumn = new BlankColumn();
		blankColumn.setDisplayClass("action-column");
		actionColumn.setTableColumn(blankColumn);
		actionColumn.setIncludeCopy(false);
		/* The results */
		final PropertyColumn idProp = new PropertyColumn(Faculty.class, "Id");
		idProp.setDisplayClass("id");
		idProp.setColumnName(new LocalizedText("Id"));
		final SearchResultColumnImpl idColumn = new SearchResultColumnImpl();
		idColumn.setTableColumn(idProp);

		final PropertyColumn fnameProp = new PropertyColumn(Faculty.class, "FirstName");
		fnameProp.setDisplayClass("fname");
		fnameProp.setColumnName(new LocalizedText("FirstName"));
		final SearchResultColumnImpl fnameColumn = new SearchResultColumnImpl();
		fnameColumn.setTableColumn(fnameProp);

		final PropertyColumn lnameProp = new PropertyColumn(Faculty.class, "LastName");
		idProp.setDisplayClass("lname");
		lnameProp.setColumnName(new LocalizedText("LastName"));
		final SearchResultColumnImpl lnameColumn = new SearchResultColumnImpl();
		lnameColumn.setTableColumn(lnameProp);

		final PropertyColumn slugProp = new PropertyColumn(Faculty.class, "Slug");
		slugProp.setDisplayClass("slug");
		slugProp.setColumnName(new LocalizedText("Slug"));
		final SearchResultColumnImpl slugColumn = new SearchResultColumnImpl();
		slugColumn.setTableColumn(slugProp);

		final PropertyColumn rankProp = new PropertyColumn(Faculty.class, "RankType");
		rankProp.setDisplayClass("rank");
		rankProp.setColumnName(new LocalizedText("JobGrade"));
		final SearchResultColumnImpl rankColumn = new SearchResultColumnImpl();
		rankColumn.setTableColumn(rankProp);

		final BlankColumn joinProp = new BlankColumn();
		joinProp.setDisplayClass("join");
		joinProp.setColumnName(new LocalizedText("JoinDate"));
		final SearchResultColumnImpl joinColumn = new SearchResultColumnImpl();
		joinColumn.setTableColumn(joinProp);
		joinColumn.setTableCellRenderer(new Label()
		{
			@NotNull
			@Override
			protected TextSource getCellValueAsText(Object value)
			{
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				if(value instanceof Faculty)
				{
					Faculty faculty = EntityRetriever.getInstance().narrowProxyIfPossible(value);
					Date date = faculty.getJoinDate();
					if(date != null)
					{
						return TextSources.create(dateFormat.format(date));
					}
				}
				return TextSources.EMPTY;
			}
		});

		final PropertyColumn areaProp = new PropertyColumn(Faculty.class, "SearchArea");
		areaProp.setDisplayClass("area");
		areaProp.setColumnName(new LocalizedText("SearchArea"));
		final SearchResultColumnImpl areaColumn = new SearchResultColumnImpl();
		areaColumn.setTableColumn(areaProp);

		final BlankColumn saProp = new BlankColumn();
		saProp.setDisplayClass("sabbatical");
		saProp.setColumnName(new LocalizedText("Sabbatical"));
		final SearchResultColumnImpl saColumn = new SearchResultColumnImpl();
		saColumn.setTableColumn(saProp);
		saColumn.setTableCellRenderer(new Label()
		{
			@NotNull
			@Override
			protected TextSource getCellValueAsText(Object value)
			{
				if (value instanceof Faculty)
				{
					Faculty faculty = EntityRetriever.getInstance().narrowProxyIfPossible(value);
					boolean sabbatical = faculty.isSabbatical();
					if (sabbatical)
					{
						return TextSources.create("Yes");
					}
					else
						return TextSources.create("No");
				}
				return TextSources.EMPTY;
			}
		});

		/** Search model. */
		final SearchModelImpl searchModel = new SearchModelImpl();
		searchModel.getConstraints().add(nameCons);
		searchModel.getConstraints().add(rankCons);
		searchModel.getResultColumns().add(actionColumn);
		searchModel.getResultColumns().add(idColumn);
		searchModel.getResultColumns().add(fnameColumn);
		searchModel.getResultColumns().add(lnameColumn);
		searchModel.getResultColumns().add(slugColumn);
		searchModel.getResultColumns().add(rankColumn);
		searchModel.getResultColumns().add(joinColumn);
		searchModel.getResultColumns().add(areaColumn);
		searchModel.getResultColumns().add(saColumn);

		//QLBuilder
		final Supplier<QLBuilder> builderSupplier = new Supplier<QLBuilder>()
		{
			@Override
			public QLBuilder get()
			{
				return _facultyDAO.getAllFacultiesQB();
			}
		};
		final SearchSupplierImpl supplier = new SearchSupplierImpl();
		supplier.setBuilderSupplier(builderSupplier);
		supplier.setSearchModel(searchModel);
		supplier.setSearchUIOperationHandler(this);

		final SearchUIImpl.Options options = new SearchUIImpl.Options((getClass().getName()));
		options.addSearchSupplier(supplier);
		//options.setAutoSearchOnConstraintChange(false);

		_searchUI = new SearchUIImpl(options);
		_searchUI.getComponent().getDisplay().addClassName("search_wrapper");
		final Container search = Container.of("search",
				new Label(FacultyManagementLOK.SEARCH_FACULTY()).setHTMLElement(HTMLElement.h1),
				_searchUI);
		mainCon.add(search);
	}

	@Override
	public boolean supportsOperation(SearchUIOperation operation)
	{
		switch(operation)
		{
			case edit:
				return true;
			case delete:
				return true;
			default:
				return false;
		}
	}

	@Override
	public void handle(SearchUIOperationContext context)
	{
		final Object rowData = context.getData();
		if(rowData == null || !(rowData instanceof Faculty)) return;
		final Faculty faculty = (Faculty) rowData;
		switch(context.getOperation())
		{
			case edit:
				_workspace.handle(new EntityWorkspaceEvent<>(this, faculty, StandardEventType.modification));
				break;
			case delete:
				_workspace.handle(new EntityWorkspaceEvent<>(this, faculty, StandardEventType.deletion));
				break;
			default:
				break;
		}
	}

	@Override
	public boolean supportsWorkspaceEvent(WorkspaceEvent event)
	{
		/*boolean flag = EnumSet.of(StandardEventType.selection, StandardEventType.modification,
				   StandardEventType.deletion, StandardEventType.creation).contains(event.getType());*/

		if(! (event instanceof EntityWorkspaceEvent))
			return false;
		EntityWorkspaceEvent ewe = (EntityWorkspaceEvent) event;
		Object entity = ewe.getEntity();
		entity = EntityRetriever.getInstance().reattachIfNecessary(entity);
		return (entity instanceof Faculty); //&& flag;
	}

	@Override
	public WorkspaceHandlerResult handle(WorkspaceEvent event, WorkspaceHandlerContext context)
	{
		if(event instanceof EntityWorkspaceEvent<?>)
		{
			final EntityWorkspaceEvent<Faculty> ewe = (EntityWorkspaceEvent<Faculty>) event;
			StandardEventType type = (StandardEventType) ewe.getType();
			switch(type)
			{
				case creation:
					return _openTaskUI(null);
				case modification:
					return _openTaskUI(ewe.getEntity());
				case deletion:
					_facultyDAO.deleteFaculty(ewe.getEntity());
					_searchUI.doAction(SearchUIAction.search);
					return new WorkspaceHandlerResult().setEventHandled(true);
				default:
					return new WorkspaceHandlerResult().setEventHandled(false);
			}
		}

		return new WorkspaceHandlerResult().setEventHandled(false);
	}

	private WorkspaceHandlerResult _openTaskUI(Faculty faculty)
	{
		//open by workspace
		final FacultyEditor editor;
		final TextSource label;
		String str;

		if(null == faculty)
		{
			editor = new FacultyEditor();
			str = "new-faculty";
			label = TextSources.create("create new faculty");
		}
		else
		{
			editor  = new FacultyEditor(faculty);
			str = faculty.getId().toString();
			label = TextSources.create("edit faculty");
		}
		//when close the component,auto perform search
		editor.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentClosed(ComponentEvent e)
			{
				_searchUI.doAction(SearchUIAction.search);
			}
		});

		_workspace.addUITask(new AbstractUITask(str)
		{
			@Override
			public TextSource getLabel()
			{
				return label;
			}

			@Override
			public Component createTaskUI(LocaleContext localContext)
			{
				return editor;
			}
		});
		return new WorkspaceHandlerResult().setEventHandled(true);
	}
}
