const { get, put } = require('../../../utils/request');

Page({
  data: {
    orders: [],
    currentStatus: '',
    page: 1,
    hasMore: true
  },

  onLoad() { this.loadOrders(); },

  async loadOrders() {
    try {
      const params = { page: this.data.page, size: 10 };
      if (this.data.currentStatus) params.status = this.data.currentStatus;
      const result = await get('/admin/order/list', params);
      this.setData({
        orders: this.data.page === 1 ? result.records : [...this.data.orders, ...result.records],
        hasMore: result.current < result.pages
      });
    } catch (e) { console.error(e); }
  },

  onStatusFilter(e) {
    this.setData({ currentStatus: e.currentTarget.dataset.status, page: 1, orders: [] });
    this.loadOrders();
  },

  async updateStatus(e) {
    const { id, status } = e.currentTarget.dataset;
    try {
      await put(`/admin/order/${id}/status?status=${status}`);
      wx.showToast({ title: '更新成功', icon: 'success' });
      this.setData({ page: 1, orders: [] });
      this.loadOrders();
    } catch (e) { console.error(e); }
  },

  onReachBottom() {
    if (this.data.hasMore) {
      this.setData({ page: this.data.page + 1 });
      this.loadOrders();
    }
  }
});
