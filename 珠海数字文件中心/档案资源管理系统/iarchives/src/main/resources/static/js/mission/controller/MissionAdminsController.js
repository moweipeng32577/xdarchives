/**
 * Created by xd on 2017/10/21.
 */
var _certEncode;              //签名证书Base64编码
var _sealImageEncode;        //签章图片Base64编码  移交
var _sealImageEncode_edit;        //签章图片Base64编码  审核
var _srcBytes;              //pdf文件Base64编码
var _sameName;//本机所用的数字证书和用户绑定证书一致
var _usrCertNO;//数字证书编号
Ext.define('Mission.controller.MissionAdminsController', {
    extend: 'Ext.app.Controller',

    //加载view
    views: [
    	'MissionAdminsView', 'DestroyTreeView', 'DestroyGridView', 'OpenGridView', 'OpenTreeView',
        'DzJyGridView','DzJyTreeView','StJyGridView','StJyTreeView','DzPrintGridView','DzPrintTreeView',
        'AuditGridView','AuditTreeView'
    ],
    //加载store
    stores: [
    	'DestroyGridStore', 'OpenGridStore', 'OpenTreeStore','DestroyTreeStore', 'DzJyGridStore',
    	'DzJyTreeStore','StJyGridStore','StJyTreeStore','AuditTreeStore','AuditGridStore'
    ],
    //加载model
    models: [
    	'MissionTreeModel', 'DestroyGridModel', 'OpenGridModel', 'MissionSelectModel','DzJyGridModel',
    	'StJyGridModel'
    ],
    init: function () {
        this.control({
            'destroyTreeView': {
            	render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        if (node.getOwnerTree().getSelectionModel().selected.length == 0) {
                            node.getOwnerTree().getSelectionModel().select(node.firstChild);
                        }
                        //destroyTreeView共用dzJyTreeStore，导致node.getOwnerTree()等于destroyTreeView，新增destroyTreeView.js解决
                    })
                },
                select: function (treemodel, record) {
                    getTreeView(treemodel, record, '销毁');
                }
            },
            'destroyGridView button[itemId=transactId]': {
                click: function (view) {
                    getMissionView(view, '销毁');
                }
            },
            'destroyGridView button[itemId=lookBillsId]': {
                click: function (view) {
                    getMissionView(view, '销毁');
                }
            },
            'openTreeView': {
                select: function (treemodel, record) {
                    getTreeView(treemodel, record, '数据开放');
                }
            },
            'openGridView button[itemId="transactId"]': {
                click: function (view) {
                    getMissionView(view, '数据开放');
                }
            },
            'openGridView button[itemId="lookBillsId"]': {
                click: function (view) {
                    getMissionView(view, '数据开放');
                }
            },
            'dzJyTreeView': {
                select: function (treemodel, record) {
                    getTreeView(treemodel, record, '查档');
                }
            },
            // 'stJyTreeView': {
            //     select: function (treemodel, record) {
            //         getTreeView(treemodel, record, '实体查档');
            //     }
            // },
            'dzJyGridView button[itemId=transactId]': {
                click: function (view) {
                    getMissionView(view, '查档');
                }
            },
            'dzJyGridView button[itemId=lookBillsId]': {
                click: function (view) {
                    getMissionView(view, '查档');
                }
            },
            // 'stJyGridView button[itemId=transactId]': {
            //     click: function (view) {
            //         getMissionView(view, '实体查档');
            //     }
            // },
            // 'stJyGridView button[itemId=lookBillsId]': {
            //     click: function (view) {
            //         getMissionView(view, '实体查档');
            //     }
            // },
            'dzPrintTreeView': {
                select: function (treemodel, record) {
                    getTreeView(treemodel, record, '电子打印');
                }
            },
            'dzPrintGridView button[itemId=lookBillsId]': {
                click: function (view) {
                    getMissionView(view, '电子打印');
                }
            },
            'dzPrintGridView button[itemId=transactId]': {
                click: function (view) {
                    getMissionView(view, '电子打印');
                }
            },
            'auditTreeView': {
                select: function (treemodel, record) {
                    getTreeView(treemodel, record, '采集移交审核');
                }
            },
            'auditGridView button[itemId=transactId]': {
                click: function (view) {
                    getMissionView(view, '采集移交审核');
                }
            },
            'auditGridView button[itemId=lookBillsId]': {
                click: function (view) {
                    getMissionView(view, '采集移交审核');
                }
            },
            'auditGridView button[itemId=print]': {
                click: function (view) {
                    var ids = [];
                    var gridView = view.findParentByType('auditGridView');
                    var select = gridView.getSelectionModel();
                    if (select.getCount() != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var taskid = select.getSelection()[0].id;
                    if(netcatUse=='1'){//已启用签章配置
                        if(_certEncode){//没有证书编号的时候，读取一下
                        }else{
                            getUserCert();//获取用户证书 Base64编码
                        }
                    }
                    var params = {};
                    ids.push(taskid);
                    if(_certEncode) {//有数字证书的才进行报表签章
                        //获取移交和审核的签章base64编码
                        //getSigncode(ids.join(","));
                        if(_sealImageEncode&&_sealImageEncode.length>200){//有移交签章才进行签章打印
                            signPrint=1;
                        }
                    }
                    var pdfData=getFileBase64(ids.join(","));
                    sessionStorage.setItem("_imgUrl", pdfData);
                    var url = '../../../js/pdfJs/web/ureportviewer.html';
                    window.open(url, '_blank');
                    return;
                    if(reportServer == 'UReport') {
                        params['docid'] = ids.join(",");
                        XD.UReportPrint(null, '移交单据管理', params);
                    } else if(reportServer == 'FReport'){
                        XD.FRprint(null, '移交单据管理', ids.length > 0 ? "'docid':'" + ids.join(",")+"'": '');
                    }
                }
            },
            'missionAdminsView':{
                render:function (view) {
                	// if (info != null) {
                	// 	XD.msg('当前数据已过期');
                	// }
                    view.down('[itemId=destroyTreeId]').on('render',function (view) {
                        view.getSelectionModel().select(view.getRootNode().childNodes[0]);
                    });
                    view.down('[itemId=openTreeId]').on('render',function (view) {
                        view.getSelectionModel().select(view.getRootNode().childNodes[0]);
                    });
                }
            }
        });
    }
});
//步骤1：获取用户证书
function getUserCert() {
    var selectType = "{\"UIFlag\":\"default\", \"InValidity\":true,\"Type\":\"signature\", \"Method\":\"device\",\"Value\":\"any\"}";
    var selectCondition = "IssuerCN~'NETCA' && InValidity='True' && CertType='Signature'";
    netca_getCertStringAttribute(null, selectType, selectCondition, -1, successGetCertStringAttributeCallBack,
        failedGetCertStringAttributeCallBack);
}
function successGetCertStringAttributeCallBack(res) {
    /* document.getElementById("signatureCreator_certEncode").value=res.certCode;
     document.getElementById("signatureCreator_certEncodeEx").value=res.certCode;*/
    _certEncode=res.certCode;
    _usrCertNO=res.AppUsrCertNO;
    /*if(caUserid==res.AppUsrCertNO){
        _sameName=1;//证书和用户一致
    }*/
    //alert("用户证书编码： "+res.certCode);
    getUserSealImage();//获取用户签章图片 Base64编码
}

function failedGetCertStringAttributeCallBack(res) {
    //alert(res.msg);
}

//步骤2：获取用户签章图片
function getUserSealImage() {//传递的参数是用户证书编码
    netca_getSealImage(_certEncode, successGetUserSealImageCallBack, failedGetUserSealImageCallBack);//传递的参数是用户证书编码
}
function successGetUserSealImageCallBack(res) {
    /*document.getElementById("signatureCreator_sealImageEncode").value=res.sealImageBase64;
    document.getElementById("signatureCreator_sealImageEncodeEx").value=res.sealImageBase64;*/
    _sealImageEncode=res.sealImageBase64;
    //alert("用户签章图片编码： "+res.sealImageBase64);
}

function failedGetUserSealImageCallBack(res) {
    alert(res.msg);
}

function getFileBase64(docid){
    var pdfData;
    Ext.Ajax.request({
        url: '/acquisition/getFileBase64',
        async:false,
        methods:'Post',
        params:{
            docid:docid,
            taskid:docid,
        },
        success: function (response) {
            XD.msg(Ext.decode(response.responseText).msg);
            pdfData= Ext.decode(response.responseText).data;
        },
        failure:function(){
            XD.msg('获取PDF源文件base64失败');
        }
    });
    return pdfData;
}


function seal_SignSealPosition(_srcBytes,_xPos,_yPos,_width) {
    var tarFilepath='';//存储路径
    var params = {
        srcFile: '',                      //源pdf文件
        srcBytes: _srcBytes,                    //源Pdf文件的Base64编码
        destFile: tarFilepath,                    //目标pdf文件
        certEncode: _certEncode,                //签名证书Base64编码
        selMode: 1,                      //操作模式
        signFieldText: '',          //签名域显示的文字
        sealImageEncode: _sealImageEncode,      //签章图片Base64编码
        revInfoIncludeFlag: false,//是否包含吊销信息
        SignPosition:                           //签名位置对象
            {
                pageNum: 1,                  //PDF文档的页码
                xPos: _xPos,                        //签名域/签章左下角的水平向右方向坐标
                yPos: _yPos,                        //签名域/签章左下角的垂直向上方向坐标
                width: _width,                      //签名域/签章的宽度
                height: _width                    //签名域/签章的高度
            },
        Tsa:                                    //时间戳对象
            {
                //tsaUrl: 'http://tsa.cnca.net/NETCATimeStampServer/TSAServer.jsp',                    //时间戳地址
                tsaUrl: '',                    //时间戳地址
                tsaUsr: '',                    //时间戳服务对应用户名
                tsaPwd: '',                    //时间戳服务对应用户的密码
                tsaHashAlgo: ''           //时间戳使用的hash算法，例如”sha-1”，”sha-256”等
            }
    };

    NetcaPKI.signatureCreatorSignSeal(params)
        .Then(function (res)
        {
            SignatureCreatorSuccessCallBack(res);
        })
        .Catch(function (res)
        {
            SignatureCreatorFailedCallBack(res);
        });
}


function generatePdf(pdfData){
    Ext.Ajax.request({
        url: '/acquisition/generatePdf',
        async:false,
        methods:'Post',
        params:{
            pdfData:pdfData,
            docid:window.docid,
            usrCertNO:_usrCertNO,
            type:2
        },
        success: function (response) {
            XD.msg('生成签章PDF成功');
        },
        failure:function(){
            XD.msg('生成签章PDF失败');
        }
    });
}

function getTreeView(treemodel, record, type) {
	if (record.get('leaf')) {
		var missionAdminsView = treemodel.view.findParentByType('missionAdminsView');
		var view;
		if (type == '销毁') {
			view = missionAdminsView.down('[itemId=destroyGridViewID]');
			window.wdestroyGridView = view;
		} else if (type == '数据开放') {
			view = missionAdminsView.down('[itemId=openGridViewID]');
			window.wopenGridView = view;
		}else if (type == '查档') {
            view = missionAdminsView.down('[itemId=dzJyGridViewID]');
            window.wdzJyGridView = view;
        }
        // else if (type == '实体查档') {
        //     view = missionAdminsView.down('[itemId=stJyGridViewID]');
        //     window.wstJyGridView = view;
        // }
        else if (type == '电子打印') {
            view = missionAdminsView.down('[itemId=dzPrintGridViewID]');
            window.wdzPrintGridView = view;
        }else if(type == '采集移交审核') {
            view = missionAdminsView.down('[itemId=auditGridViewID]');
            window.wauditGridView = view;
        }
	    var tbarButtons = view.getDockedItems("toolbar[dock=top]")[0].items.items;
	    var selectedtext = treemodel.selected.items[0].get('text');
	    if (selectedtext != '待处理') {
            for (var i = 0; i < tbarButtons.length; i++) {
                if ('lookBillsId' == tbarButtons[i].itemId) {
                    tbarButtons[i].show();
                } else {
                    tbarButtons[i].hide();
                }
            }
            if(type == '采集移交审核'){
                view.down('[itemId=print]').setHidden(false);
            }
            getColumnShow(view);
        }else {
	        for (var i = 0; i < tbarButtons.length; i++) {
	            if('lookBillsId' == tbarButtons[i].itemId || typeof(tbarButtons[i].itemId) == 'undefined'){
	                tbarButtons[i].hide();
	            }else{
	                tbarButtons[i].show();
	            }
	        }
            if(type == '采集移交审核'){
                view.down('[itemId=print]').setHidden(true);
            }
	        getColumnHide(view);
	    }
		if (type == '销毁') {
			window.wdestroyGridView.treeItem = selectedtext;
	        view.initGrid({state: selectedtext, type: '销毁'});
		} else if (type == '数据开放') {
			window.wopenGridView.treeItem = selectedtext;
	        view.initGrid({state: selectedtext, type: '数据开放'});
		}else if (type == '查档') {
            window.wdzJyGridView.treeItem = selectedtext;
            view.initGrid({state: selectedtext, type: '查档'});
        }
        // else if (type == '实体查档') {
        //     window.wstJyGridView.treeItem = selectedtext;
        //     view.initGrid({state: selectedtext, type: '实体查档'});
        // }
        else if (type == '电子打印') {
            window.wdzPrintGridView.treeItem = selectedtext;
            view.initGrid({state: selectedtext, type: '电子打印'});
        }else if(type == '采集移交审核'){
            window.wauditGridView.treeItem = selectedtext;
            view.initGrid({state: selectedtext, type: '采集移交审核'});
        }
	}
}
function getColumnShow(view) {
	view.columns[2].show();
	view.columns[3].show();
	view.columns[4].show();
	return view;
}
function getColumnHide(view) {
	view.columns[2].hide();
	view.columns[3].hide();
	view.columns[4].hide();
	return view;
}
function getMissionView(view, type) {
	var gridView,html;
	if (type == '销毁') {
		gridView = view.findParentByType('destroyGridView');//数据销毁
	} else if (type == '数据开放') {
		gridView = view.findParentByType('openGridView');//数据开放
	}else if (type == '查档') {
        gridView = view.findParentByType('dzJyGridView');//查档
    }
    // else if (type == '实体查档') {
    //     gridView = view.findParentByType('stJyGridView');//实体查档
    // }
    else if (type == '电子打印') {
        gridView = view.findParentByType('dzPrintGridView');//电子打印
    }else if(type == '采集移交审核'){
        gridView = view.findParentByType('auditGridView');//采集移交审核
    }
    var select = gridView.getSelectionModel();
    if (select.getCount() != 1) {
        XD.msg('请选择一条数据');
        return;
    }
    var taskid = select.getSelection()[0].get('id');
    window.wgridView = gridView;
    if (type == '销毁') {
		html = '<iframe id="frame1" src="/destructionBill/billApproval?flag=1&taskid=' + taskid + '&type=' + encodeURIComponent(gridView.treeItem) + '" frameborder="0" width="100%" height="100%"></iframe>';
	} else if (type == '数据开放') {
		html = '<iframe id="frame1" taskid=' + taskid + ' src="/openApprove/main?flag=1&taskid=' + taskid + '&type=' + encodeURIComponent(gridView.treeItem) + '"  frameborder="0" width="100%" height="100%"></iframe>';
	}else if (type == '查档') {
        html = '<iframe id="frame1" src="/electronApprove/main?flag=1&taskid=' + taskid + '&type=' + encodeURIComponent(gridView.treeItem) + '"  frameborder="0" width="100%" height="100%"></iframe>';
    }
    // else if (type == '实体查档') {
    //     html = '<iframe id="frame1" src="/stApprove/main?flag=1&taskid=' + taskid + '&type=' + encodeURIComponent(gridView.treeItem) + '" frameborder="0" width="100%" height="100%"></iframe>';
    // }
    else if (type == '电子打印') {
        html = '<iframe id="frame1" src="/electronPrintApprove/main?flag=1&taskid=' + taskid + '&type=' + encodeURIComponent(gridView.treeItem) + '" frameborder="0" width="100%" height="100%"></iframe>';
    } else if(type == '采集移交审核'){
        html = '<iframe id="frame1" src="/audit/mainDeal?flag=1&taskid=' + taskid + '&type=' + encodeURIComponent(gridView.treeItem) +'" frameborder="0" width="100%" height="100%"></iframe>';
    }
    window.approve = Ext.create("Ext.window.Window", {
        width: '100%',
        height: '100%',
        modal: true,
        header: false,
        draggable: false,//禁止拖动
        resizable: false,//禁止缩放
        html:html,
        docGrid:gridView
    }).show();
    Ext.on('resize',function(a,b){
        window.approve.setPosition(0, 0);
        window.approve.fitContainer();
    });
  // setTimeout(function () {
  //     $('#frame1').contents().find("head").append(parent.closeObj.getStyle());
  // },500);
}
function fun(){
    var params={taskid: document.getElementById('frame1').getAttribute('taskid')};
    document.getElementById('frame1').contentWindow.opengrid.initGrid(params);
}