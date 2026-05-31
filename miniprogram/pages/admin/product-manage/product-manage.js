const { get, del } = require('../../../utils/request');

Page({
  data: {
    products: [],
    page: 1,
    hasMore: true
  },

  onLoad() { this.loadProducts(); },
  onShow() { this.setData({ page: 1, products: [] }); this.loadProducts(); },

  async loadProducts() {
    try {
      const result = await get('/product/list', { page: this.data.page, size: 20 });
      this.setData({
        products: this.data.page === 1 ? result.records : [...this.data.products, ...result.records],
        hasMore: result.current < result.pages
      });
    } catch (e) { console.error(e); }
  },

  async deleteProduct(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除此商品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await del(`/admin/product/${id}`);
            wx.showToast({ title: '已删除', icon: 'success' });
            this.setData({ page: 1, products: [] });
            this.loadProducts();
          } catch (e) { console.error(e); }
        }
      }
    });
  },

  onReachBottom() {
    if (this.data.hasMore) {
      this.setData({ page: this.data.page + 1 });
      this.loadProducts();
    }
  }
});
