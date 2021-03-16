/**
 * Created by Administrator on 2019/6/27.
 */


Ext.define('ClassifySearchDirectory.model.ClassifySearchDirectoryTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});
