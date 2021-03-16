/*
 * Note that this control will most likely remain as an example, and not as a core Ext form
 * control.  However, the API will be changing in a future release and so should not yet be
 * treated as a final, stable API at this time.
 */

/**
 * A control that allows selection of between two Ext.ux.form.MultiSelect controls.
 */
Ext.define('Ext.ux.form.ItemSelector', {
    extend: 'Ext.ux.form.MultiSelect',
    alias: ['widget.itemselectorfield', 'widget.itemselector'],
    alternateClassName: ['Ext.ux.ItemSelector'],
    requires: [
        'Ext.button.Button',
        'Ext.ux.form.MultiSelect'
    ],

    /**
     * @cfg {Boolean} [hideNavIcons=false] True to hide the navigation icons
     */
    hideNavIcons:false,

    /**
     * @cfg {Array} buttons Defines the set of buttons that should be displayed in between the ItemSelector
     * fields. Defaults to <tt>['top', 'up', 'add', 'remove', 'down', 'bottom']</tt>. These names are used
     * to build the button CSS class names, and to look up the button text labels in {@link #buttonsText}.
     * This can be overridden with a custom Array to change which buttons are displayed or their order.
     */
    buttons: ['top', 'up', 'add', 'remove', 'down', 'bottom'],

    /**
     * @cfg {Object} buttonsText The tooltips for the {@link #buttons}.
     * Labels for buttons.
     */
    buttonsText: {
        top: "移至顶部",
        up: "上移",
        add: "添加",
        remove: "移除",
        down: "下移",
        bottom: "移至底部"
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    
    ariaRole: 'group',

    initComponent: function() {
        var me = this;

        me.ddGroup = me.id + '-dd';
        me.ariaRenderAttributes = me.ariaRenderAttributes || {};
        me.ariaRenderAttributes['aria-labelledby'] = me.id + '-labelEl';
        
        me.callParent();

        // bindStore must be called after the fromField has been created because
        // it copies records from our configured Store into the fromField's Store
        me.bindStore(me.store);
    },

    createList: function(title){
        var me = this;

        return Ext.create('Ext.ux.form.MultiSelect', {
            // We don't want the multiselects themselves to act like fields,
            // so override these methods to prevent them from including
            // any of their values
            submitValue: false,
            getSubmitData: function(){
                return null;
            },
            getModelData: function(){
                return null;    
            },
            flex: 1,
            dragGroup: me.ddGroup,
            dropGroup: me.ddGroup,
            title: title,
            store: {
                model: me.store.model,
                data: []
            },
            displayField: me.displayField,
            valueField: me.valueField,
            disabled: me.disabled,
            listeners: {
                boundList: {
                    scope: me,
                    itemdblclick: me.onItemDblClick,
                    drop: me.syncValue
                }
            }
        });
    },

    setupItems: function() {
        var me = this;

        me.fromField = me.createList(me.fromTitle);
        me.toField = me.createList(me.toTitle);

        return [
            me.fromField,
            {
                xtype: 'toolbar',
                margin: '0 4',
                padding: 0,
                layout: {
                    type: 'vbox',
                    pack: 'center'
                },
                items: me.createButtons()
            },
            me.toField
        ];
    },

    createButtons: function() {
        var me = this,
            buttons = [];

        if (!me.hideNavIcons) {
            Ext.Array.forEach(me.buttons, function(name) {
                buttons.push({
                    xtype: 'button',
                    ui: 'default',
                    tooltip: me.buttonsText[name],
                    ariaLabel: me.buttonsText[name],
                    handler: me['on' + Ext.String.capitalize(name) + 'BtnClick'],
                    cls: Ext.baseCSSPrefix + 'form-itemselector-btn',
                    iconCls: Ext.baseCSSPrefix + 'form-itemselector-' + name,
                    navBtn: true,
                    scope: me,
                    margin: '4 0 0 0'
                });
            });
        }
        return buttons;
    },

    /**
     * Get the selected records from the specified list.
     * 
     * Records will be returned *in store order*, not in order of selection.
     * @param {Ext.view.BoundList} list The list to read selections from.
     * @return {Ext.data.Model[]} The selected records in store order.
     * 
     */
    getSelections: function(list) {
        var store = list.getStore();

        return Ext.Array.sort(list.getSelectionModel().getSelection(), function(a, b) {
            a = store.indexOf(a);
            b = store.indexOf(b);

            if (a < b) {
                return -1;
            } else if (a > b) {
                return 1;
            }
            return 0;
        });
    },

    onTopBtnClick : function() {
        var list = this.toField.boundList,
            store = list.getStore(),
            selected = this.getSelections(list);

        if(typeof(nodeid) !="undefined") {  //判断是否工作流-设置环节用户
            var userid = "";
            for (var i=0;i < selected.length; i++) {
                if (userid == '') {
                    userid += (selected[i].data.userid) + "-";
                } else {
                    userid += (selected[i].data.userid);
                }
            }
            if(userid!=""){
                //更新临时表序号
                Ext.Ajax.request({
                    params: {
                        userid: userid,
                        nodeid: nodeid,
                        type:"top"
                    },
                    url: '/user/updateUserNodeTemp',
                    method: 'POST',
                    sync: true,
                    success: function (resp) {}
                })
            }
        }
        store.suspendEvents();
        store.remove(selected, true);
        store.insert(0, selected);
        store.resumeEvents();
        list.refresh();
        this.syncValue(); 
        list.getSelectionModel().select(selected);
    },

    onBottomBtnClick : function() {
        var list = this.toField.boundList,
            store = list.getStore(),
            selected = this.getSelections(list);

        if(typeof(nodeid) !="undefined") {  //判断是否工作流-设置环节用户
            var userid = "";
            for (var i = 0; i < selected.length; i++) {
                if (userid == '') {
                    userid += (selected[i].data.userid) + "-";
                } else {
                    userid += (selected[i].data.userid);
                }
            }
            if (userid != "") {
                //更新临时表序号
                Ext.Ajax.request({
                    params: {
                        userid: userid,
                        nodeid: nodeid,
                        type: "bottom"
                    },
                    url: '/user/updateUserNodeTemp',
                    method: 'POST',
                    sync: true,
                    success: function (resp) {
                    }
                })
            }
        }
        store.suspendEvents();
        store.remove(selected, true);
        store.add(selected);
        store.resumeEvents();
        list.refresh();
        this.syncValue();
        list.getSelectionModel().select(selected);
    },

    onUpBtnClick : function() {
        var list = this.toField.boundList,
            store = list.getStore(),
            selected = this.getSelections(list),
            rec,
            i = 0,
            len = selected.length,
            index = 0;
        var userid = "";

        // Move each selection up by one place if possible
        store.suspendEvents();
        for (; i < len; ++i, index++) {
            rec = selected[i];
            index = Math.max(index, store.indexOf(rec) - 1);
            store.remove(rec, true);
            store.insert(index, rec);
            if (userid == '') {
                userid += (selected[i].data.userid) + "-";
            } else {
                userid += (selected[i].data.userid);
            }
        }
        if(typeof(nodeid) !="undefined") {  //判断是否工作流-设置环节用户
            if (userid != "") {
                //更新临时表序号
                Ext.Ajax.request({
                    params: {
                        userid: userid,
                        nodeid: nodeid,
                        type: "up"
                    },
                    url: '/user/updateUserNodeTemp',
                    method: 'POST',
                    sync: true,
                    success: function (resp) {
                    }
                })
            }
        }
        store.resumeEvents();
        list.refresh();
        this.syncValue();
        list.getSelectionModel().select(selected);

    },

    onDownBtnClick : function() {
        var list = this.toField.boundList,
            store = list.getStore(),
            selected = this.getSelections(list),
            rec,
            i = selected.length - 1,
            index = store.getCount() - 1;

        // Move each selection down by one place if possible
        store.suspendEvents();
        var userid = "";
        for (; i > -1; --i, index--) {
            rec = selected[i];
            index = Math.min(index, store.indexOf(rec) + 1);
            store.remove(rec, true);
            store.insert(index, rec);
            if (userid == '') {
                userid += (selected[i].data.userid) + "-";
            } else {
                userid += (selected[i].data.userid);
            }
        }
        if(typeof(nodeid) !="undefined") {  //判断是否工作流-设置环节用户
            if (userid != "") {
                //更新临时表序号
                Ext.Ajax.request({
                    params: {
                        userid: userid,
                        nodeid: nodeid,
                        type: "down"
                    },
                    url: '/user/updateUserNodeTemp',
                    method: 'POST',
                    sync: true,
                    success: function (resp) {
                    }
                })
            }
        }
        store.resumeEvents();
        list.refresh();
        this.syncValue();
        list.getSelectionModel().select(selected);
    },

    onAddBtnClick : function() {
        var me = this,
            selected = me.getSelections(me.fromField.boundList);

        me.moveRec(true, selected, 'click', selected);
        me.toField.boundList.getSelectionModel().select(selected);
    },

    onRemoveBtnClick : function() {
        var me = this,
            selected = me.getSelections(me.toField.boundList);

        me.moveRec(false, selected, 'click', selected);
        me.fromField.boundList.getSelectionModel().select(selected);
    },

    // operate(操作)，seletedInfo(选择的数据)
    moveRec: function(add, recs, operate, seletedInfo) {
        var me = this,
            fromField = me.fromField,
            toField   = me.toField,
            fromStore = add ? fromField.store : toField.store,
            toStore   = add ? toField.store   : fromField.store;

        fromStore.suspendEvents();
        toStore.suspendEvents();
        fromStore.remove(recs);
        toStore.add(recs);
        fromStore.resumeEvents();
        toStore.resumeEvents();
        
        // If the list item was focused when moved (e.g. via double-click)
        // then removing it will cause the focus to be thrown back to the
        // document body. Which might disrupt things if ItemSelector is
        // contained by a floating thingie like a Menu.
        // Focusing the list itself will prevent that.
        if (fromField.boundList.containsFocus) {
            fromField.boundList.focus();
        }
        $(".copyFunSelect").text("已选");//用户管理-复制权限显示已选人数
        if(typeof(isCopyFun)!="undefined"){
            var toFieldlen=toField.store.data.length;
            $(".copyFunSelect").text("已选（"+toFieldlen+"）")
        }
        fromField.boundList.refresh();
        // toField.boundList.refresh();

        me.syncValue();

        var userid = "";
        if (operate == 'click') {
        	var selected = seletedInfo;
        	for (var i = 0; i < selected.length; i++) {
                if (i != selected.length-1) {
                    userid += (selected[i].data.userid) + "-";
                } else {
                    userid += (selected[i].data.userid);
                }
            }
        } else {
            userid = recs.data.userid;
        }
        if(typeof(nodeid) !="undefined") {  //判断是否工作流-设置环节用户
            Ext.MessageBox.wait('正在处理请稍后...','提示');
            if (add) {
                Ext.Ajax.request({
                    params: {
                        userid: userid,
                        nodeid: nodeid
                    },
                    url: '/user/addUserNodeTemp',
                    method: 'POST',
                    sync: true,
                    success: function (resp) {
                        Ext.MessageBox.hide();
                    }
                })
            } else {
                // 删除临时表中的已选用户数据
                Ext.Ajax.request({
                    params: {
                        userid: userid,
                        nodeid: nodeid
                    },
                    url: '/user/deleteUserNodeTemp',
                    method: 'POST',
                    sync: true,
                    success: function (resp) {
                        Ext.MessageBox.hide();
                    }
                })
            }
        }
        toField.boundList.refresh();
    },

    // Synchronizes the submit value with the current state of the toStore
    syncValue: function() {
        var me = this; 
        me.mixins.field.setValue.call(me, me.setupValue(me.toField.store.getRange()));
    },

    onItemDblClick: function(view, rec) {
        this.moveRec(view === this.fromField.boundList, rec, 'double');
    },

    setValue: function(value) {
        var me = this,
            fromField = me.fromField,
            toField = me.toField,
            fromStore = fromField.store,
            toStore = toField.store,
            selected,
            valuefield = me.fromField.valueField;

        // Wait for from store to be loaded
        if (!me.fromStorePopulated) {
            me.fromField.store.on({
                load: Ext.Function.bind(me.setValue, me, [value]),
                single: true
            });
            return;
        }

        value = me.setupValue(value);
        me.mixins.field.setValue.call(me, value);

        selected = me.getRecordsForValue(value);

        // Clear both left and right Stores.
        // Both stores must not fire events during this process.
        fromStore.suspendEvents();
        toStore.suspendEvents();
        fromStore.removeAll();
        /**
         * 因右边列表的数据是根据左边列表的数据加载
         * 当切换左边的数据时，右边列表的数据也会清空重新加载
         * 造成提交数据时会清楚已有的数据
         * 故不清空右边数据
         */
        //toStore.removeAll();

        // Reset fromStore
        me.populateFromStore(me.store);

        // Copy selection across to toStore
        Ext.Array.forEach(selected, function(rec){
            // In the from store, move it over
            if (fromStore.indexOf(rec) > -1) {
                fromStore.remove(rec);
            }
            /**
             * 因右边列表的数据未清空
             * 已有数据则不添加
             */
            if(toStore.find(valuefield, rec.data[valuefield]) == -1){
                toStore.add(rec);
            }
        });
        /**
         * 如果已选择数据未保存
         * 在切换几个刷新可选数据时，已选数据从可选数据中清除
         */
        for(var i=0;i<toStore.getCount();i++){
            var fromrec = fromStore.query(valuefield, toStore.getAt(i).get(valuefield));
            if(fromrec.length > 0){
                Ext.Array.forEach(fromrec.items, function(rec){
                    fromStore.remove(rec);
                });
            }
        }

        // Stores may now fire events
        fromStore.resumeEvents();
        toStore.resumeEvents();

        // Refresh both sides and then update the app layout
        Ext.suspendLayouts();
        fromField.boundList.refresh();
        toField.boundList.refresh();
        Ext.resumeLayouts(true);        
    },

    onBindStore: function(store, initial) {
        var me = this,
            fromField = me.fromField,
            toField = me.toField;

        if (fromField) {
            fromField.store.removeAll();
            toField.store.removeAll();

            if (store.autoCreated) {
                fromField.resolveDisplayField();
                toField.resolveDisplayField();
                me.resolveDisplayField();
            }

            if (!Ext.isDefined(me.valueField)) {
                me.valueField = me.displayField;
            }

            // Add everything to the from field as soon as the Store is loaded
            if (store.getCount()) {
                me.populateFromStore(store);
            } else {
                me.store.on('load', me.populateFromStore, me);
            }
        }
    },

    populateFromStore: function(store) {
        var fromStore = this.fromField.store;

        // Flag set when the fromStore has been loaded
        this.fromStorePopulated = true;

        fromStore.add(store.getRange());

        // setValue waits for the from Store to be loaded
        fromStore.fireEvent('load', fromStore);
    },

    onEnable: function(){
        var me = this;

        me.callParent();
        me.fromField.enable();
        me.toField.enable();

        Ext.Array.forEach(me.query('[navBtn]'), function(btn){
            btn.enable();
        });
    },

    onDisable: function(){
        var me = this;

        me.callParent();
        me.fromField.disable();
        me.toField.disable();

        Ext.Array.forEach(me.query('[navBtn]'), function(btn){
            btn.disable();
        });
    },

    doDestroy: function(){
        this.bindStore(null);
        this.callParent();
    }
});
