const { get } = require('../../utils/request');

Page({
  data: {
    tabs: [
      { label: '全部', status: '' },
      { label: '待配送', status: 'PAID' },
      { label: '配送中', status: 'DELIVERING' },
      { label: '已完成', status: 'COMPLETED' },
      { label: '已取消', status: 'CANCELLED' }
    ],
    currentTab: 0,
    orders: [],
    page: 1,
    hasMore: true
  },

  onLoad() {
    this.loadOrders();
  },

  onShow() {
    this.setData({ page: 1 });
    this.loadOrders();
  },

  async loadOrders() {
    try {
      const status = this.data.tabs[this.data.currentTab].status;
      const params = { page: this.data.page, size: 10 };
      if (status) params.status = status;

      const result = await get('/order/list', params);
      this.setData({
        orders: this.data.page === 1 ? result.records : [...this.data.orders, ...result.records],
        hasMore: result.current < result.pages
      });
    } catch (e) {
      console.error('加载订单失败', e);
    }
  },

  onTabChange(e) {
    this.setData({
      currentTab: e.currentTarget.dataset.index,
      page: 1,
      orders: []
    });
    this.loadOrders();
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` });
  },

  onReachBottom() {
    if (this.data.hasMore) {
      this.setData({ page: this.data.page + 1 });
      this.loadOrders();
    }
  }
});
