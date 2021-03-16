/*
 * 标准列表控件
 * 默认由查询栏、数据栏（包含按钮组）、分页栏
*/
Ext.define('Ext.xd.grid.StandardGridPanel', {
    extend: 'Ext.grid.Panel',

    alias: 'standardpanel',

    /*配置项*/
    hasSearch:true,     //查询栏
    hasPage:true,       //分页栏
    hasCheck:true,      //选择列

    initComponent: function(config) {
    	
    	if(hasSearch){
    		initSearchPanel();
    	}
    	if(hasPage){
    		initPagePanel();
    	}
    	if(hasCheck){
    		initCheckColumn();
    	}
    },

    initSearchPanel: function() {
    	
    },

    initPagePanel: function() {
    	
    },

    initCheckColumn: function() {
    	
    }
});