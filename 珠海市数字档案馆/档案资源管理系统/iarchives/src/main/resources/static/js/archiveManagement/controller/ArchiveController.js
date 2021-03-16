/**
 * 档案管理控制器
 */
Ext.define('ArchiveManagement.controller.ArchiveController', {
    extend: 'Ext.app.Controller',

    views: [
    	'ArchiveView', 'ArchiveTreeView', 'ArchivePromptView',
    	'ArchiveOutAddFormView',
    	'ArchiveGridView'
    ],//加载view
    stores: [
    	'ArchiveTreeStore', 'ArchiveGridStore'
    ],//加载store
    models: [
    	'ArchiveTreeModel', 'ArchiveGridModel'
    ],//加载model
    init: function () {
    	var ifShowRightPanel = false;
    	this.control({
    		'archiveTreeView': {
                select: function (treemodel, record) {
                    //通过当前控件找出父控件
                    var archiveView = treemodel.view.findParentByType('archiveView');
                    //找出子控件
                    var archivePromptView = archiveView.down('[itemId=archivePromptViewId]');
                    if (!ifShowRightPanel) {
                        archivePromptView.removeAll();
                        archivePromptView.add({
                            xtype: 'archiveGridView'
                        });
                        ifShowRightPanel = true;
                    }
                    var usergrid = archivePromptView.down('[itemId=archiveGridViewID]');
                    usergrid.setTitle("当前位置：" + record.get('text'));
                    window.wuserGridView = usergrid;
                    window.wuserGridView.nodename =  record.get('text');
                    window.wuserGridView.treeNodeid = record.get('fnid');
                    //ifSearchLeafNode设置是否查询当前节点下的叶子节点，ifContainSelfNode设置是否查询当前节点下的非叶子节点及当前非叶子节点
                    usergrid.initGrid({organName:record.get('text'), organID: record.get('fnid'),ifSearchLeafNode:true,ifContainSelfNode:true});
                }
            },
            'archiveGridView button[itemId=userRegister]': {//外来人员查档登记
                click: function () {
                    if(window.wuserGridView.treeNodeid==='0')  {
                        XD.msg('请选择有效机构节点');
                        return;
                    }
                    Ext.create('ArchiveManagement.view.ArchiveOutAddFormView').show();
                }
            },
            'archiveOutAddFormView button[itemId=userOutAddSubmit]': {//新增外来人员临时用户
                click: function (view) {
                    var form = view.findParentByType('archiveOutAddFormView').down('form');
                    var URL = '/user/userOutAddSubmit';
                    if (view.findParentByType('archiveOutAddFormView').title == '修改用户') {
                        URL = '/user/userOutEditSubmit'
                    }
                    var data = form.getValues();
                    if (data['realname'] === '安全保密管理员' || data['realname'] === '系统管理员' || data['realname'] === '安全审计员') {
                        XD.msg('请勿使用该用户姓名');
                        return;
                    }
                    if (data['loginname'] == '' || data['realname'] == '') {
                        XD.msg('有必填项未填写');
                        return;
                    }
                    if (data['loginname'].length<2) {
                        XD.msg('帐号字段输入长度不应少于2位');
                        return;
                    }
                    if (data['loginname'].length>30) {
                        XD.msg('帐号字段输入长度超限');
                        return;
                    }
                    if (data['realname'].length>10) {
                        XD.msg('用户姓名字段输入长度超限');
                        return;
                    }
                    if (data['outuserstarttime']) {
                    	
                    }

                    form.submit({
                        waitTitle: '提示',// 标题
                        waitMsg: '正在提交数据请稍后...',// 提示信息
                        url: URL,
                        method: 'POST',
                        params: { // 此处可以添加额外参数
                            treetext: window.wuserGridView.treeNodeid
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                view.findParentByType('archiveOutAddFormView').close();//添加成功后关闭窗口
                                window.wuserGridView.getStore().reload();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                        }
                    });
                }
            },
            'archiveOutAddFormView button[itemId=userOutAddClose]': {//外来人员查档登记 返回
                click: function (view) {
                    view.findParentByType('archiveOutAddFormView').close();
                }
            }
    	})
    }
})