/**
 * Created by Administrator on 2019/5/17.
 */

Ext.define('ThematicUtilize.view.ThematicUtilizeTreeView',{
    extend:'Ext.tree.Panel',
    xtype:'ThematicUtilizeTreeView',
    store:'ThematicUtilizethematictypeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});

