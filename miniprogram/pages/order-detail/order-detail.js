const { get, put } = require('../../utils/request');

Page({
  data: {
    order: null
  },

  onLoad(options) {
    if (options.id) {
      this.loadOrder(options.id);
    }
  },

  async loadOrder(id) {
    try {
      const order = await get(`/order/${id}`);
      this.setData({ order });
    } catch (e) {
      console.error('加载订单详情失败', e);
    }
  },

  async cancelOrder() {
    wx.showModal({
      title: '取消订单',
      content: '确定要取消此订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await put(`/order/${this.data.order.id}/cancel`);
            wx.showToast({ title: '已取消', icon: 'success' });
            this.loadOrder(this.data.order.id);
          } catch (e) {
            console.error('取消订单失败', e);
          }
        }
      }
    });
  },

  getStatusText(status) {
    const map = {
      'PENDING': '待付款', 'PAID': '待配送', 'DELIVERING': '配送中',
      'COMPLETED': '已完成', 'CANCELLED': '已取消'
    };
    return map[status] || status;
  }
});
