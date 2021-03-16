/**
 * Created by yl on 2017/11/13.
 */
Ext.define('Appraisal.model.AppraisalTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});