const { get, post } = require('../../../utils/request');

Page({
  data: {
    inventories: [],
    alerts: [],
    showAlerts: false
  },

  onLoad() {
    this.loadInventory();
    this.loadAlerts();
  },

  async loadInventory() {
    try {
      const result = await get('/admin/inventory/list', { page: 1, size: 100 });
      this.setData({ inventories: result.records });
    } catch (e) { console.error(e); }
  },

  async loadAlerts() {
    try {
      const result = await get('/admin/restock/alerts', { status: 'PENDING', page: 1, size: 100 });
      this.setData({ alerts: result.records });
    } catch (e) { console.error(e); }
  },

  toggleAlerts() {
    this.setData({ showAlerts: !this.data.showAlerts });
  },

  async restock(e) {
    const { productId } = e.currentTarget.dataset;
    wx.showModal({
      title: '入库',
      editable: true,
      placeholderText: '请输入入库数量',
      success: async (res) => {
        if (res.confirm && res.content) {
          try {
            await post(`/admin/inventory/${productId}/restock`, { quantity: parseInt(res.content) });
            wx.showToast({ title: '入库成功', icon: 'success' });
            this.loadInventory();
          } catch (e) { console.error(e); }
        }
      }
    });
  },

  async checkRestock() {
    try {
      await post('/admin/restock/check');
      wx.showToast({ title: '检查完成', icon: 'success' });
      this.loadAlerts();
    } catch (e) { console.error(e); }
  }
});
