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
import com.google.common.base.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;

import com.i2rd.cms.bean.MIWTBeanConfig;
import com.i2rd.cms.bean.MIWTStateEvent;
import com.i2rd.cms.miwt.BlankColumn;
import com.i2rd.cms.miwt.SiteAwareMIWTApplication;

import net.proteusframework.core.hibernate.dao.EntityRetriever;
import net.proteusframework.core.locale.LocalizedText;
import net.proteusframework.ui.column.PropertyColumn;
import net.proteusframework.ui.miwt.HistoryElement;
import net.proteusframework.ui.miwt.component.Container;
import net.proteusframework.ui.miwt.component.composite.HistoryContainer;
import net.proteusframework.ui.search.ActionColumn;
import net.proteusframework.ui.search.PropertyConstraint;
import net.proteusframework.ui.search.QLBuilder;
import net.proteusframework.ui.search.SearchModelImpl;
import net.proteusframework.ui.search.SearchResultColumnImpl;
import net.proteusframework.ui.search.SearchSupplierImpl;
import net.proteusframework.ui.search.SearchUIImpl;
import net.proteusframework.ui.search.SearchUIOperation;
import net.proteusframework.ui.search.SearchUIOperationContext;
import net.proteusframework.ui.search.SearchUIOperationHandler;
import net.proteusframework.ui.search.SimpleConstraint;

/**
 * View the specified faculty.
 *
 * @author Yumei Shi (yshi@venturetechasia.net)
 * @since 1/29/15 12:49 AM
 */
@MIWTBeanConfig(value = FacultyView.RESOURCE_NAME, displayName = "Faculty View:search ui",
    applicationClass = SiteAwareMIWTApplication.class, stateEvents = {
    @MIWTStateEvent(eventName = SiteAwareMIWTApplication.SAMA_ADD_COMPONENT, eventValue = FacultyView.RESOURCE_NAME ),
    @MIWTStateEvent(eventName = SiteAwareMIWTApplication.SAMA_RECREATE_ON_SITE_CHANGE, eventValue = "true")})
@Scope("prototype")
@Configurable
public class FacultyView extends HistoryContainer implements SearchUIOperationHandler
{
    /** The resource name. */
    final static String RESOURCE_NAME = "com.example.app.ui.university.FacultyView";
    /* The faculty DAO. **/
    @Autowired
    private FacultyDAO _facultyDAO;

    @Override
    public void init()
    {
        super.init();
        _setupUI();
    }

    @Override
    public boolean supportsOperation(SearchUIOperation operation)
    {
        switch (operation)
        {
            case view:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void handle(SearchUIOperationContext context)
    {
        final Object rowData = EntityRetriever.getInstance().narrowProxyIfPossible(context.getData());
        if(rowData == null || !(rowData instanceof Faculty)) return;
        final Faculty faculty = (Faculty) rowData;
        switch(context.getOperation())
        {
            case view:
                FacultyDetail detail = new FacultyDetail(faculty);
                navigateBackOnClose(detail);
                getHistory().add(new HistoryElement(detail));
                break;
            default:
                break;
        }
    }

    private void _setupUI()
    {
        removeAllComponents();
        addClassName("faculty-view");

        /* The action column */
        final ActionColumn actionColumn = new ActionColumn();
        final BlankColumn blankColumn = new BlankColumn();
        blankColumn.setDisplayClass("action-column");
        actionColumn.setTableColumn(blankColumn);
        actionColumn.setIncludeCopy(false);
        /** Results. */
        final PropertyColumn idProp = new PropertyColumn(Faculty.class, "Id");
        idProp.setDisplayClass("id");
        final SearchResultColumnImpl idColumn = new SearchResultColumnImpl().withTableColumn(idProp
            .withColumnName(new LocalizedText("Id")));

        final PropertyColumn nameProp = new PropertyColumn(Faculty.class, "FirstName");
        nameProp.setDisplayClass("name");
        final SearchResultColumnImpl nameColumn = new SearchResultColumnImpl().withTableColumn(nameProp
            .withColumnName(new LocalizedText("Name")));

        final PropertyColumn areaProp = new PropertyColumn(Faculty.class, "SearchArea");
        areaProp.setDisplayClass("area");
        final SearchResultColumnImpl areaColumn = new SearchResultColumnImpl().withTableColumn(areaProp
            .withColumnName(new LocalizedText("SearchArea")));


        SearchModelImpl searchModel = new SearchModelImpl();
        searchModel.getConstraints().add(
            new SimpleConstraint().withLabel(new LocalizedText("name:")).withProperty("firstName").withOperator(
                PropertyConstraint.Operator.like));
        searchModel.getResultColumns().add(actionColumn);
        searchModel.getResultColumns().add(idColumn);
        searchModel.getResultColumns().add(nameColumn);
        searchModel.getResultColumns().add(areaColumn);

        /* The search supplier. */
        Supplier<QLBuilder> supplier1 = _facultyDAO::getAllFacultiesQB;
        final SearchSupplierImpl supplier = new SearchSupplierImpl();
        supplier.setBuilderSupplier(supplier1);
        supplier.setSearchModel(searchModel);
        supplier.setSearchUIOperationHandler(this);

        final SearchUIImpl.Options options = new SearchUIImpl.Options(getClass().getName());
        options.addSearchSupplier(supplier);

        /* The search ui. */
        SearchUIImpl searchUI = new SearchUIImpl(options);
        searchUI.getComponent().getDisplay().addClassName("search_wrapper");
        setDefaultComponent(searchUI);
    }
}
