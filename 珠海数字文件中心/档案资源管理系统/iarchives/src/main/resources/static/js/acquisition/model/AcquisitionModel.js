/**
 * Created by Rong on 2017/10/24.
 */
Ext.define('Acquisition.model.AcquisitionModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string"},
             {name: "text", type: "string"},
             {name: "leaf", type: "boolean"}]
});