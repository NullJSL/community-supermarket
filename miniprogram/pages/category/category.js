const { get } = require('../../utils/request');
const cart = require('../../utils/cart');

Page({
  data: {
    categories: [],
    products: [],
    currentIndex: 0,
    loading: false
  },

  onLoad() {
    this.loadCategories();
  },

  async loadCategories() {
    try {
      const categories = await get('/category/list');
      this.setData({ categories });
      if (categories.length > 0) {
        this.loadProducts(categories[0].id);
      }
    } catch (e) {
      console.error('加载分类失败', e);
    }
  },

  async loadProducts(categoryId) {
    this.setData({ loading: true });
    try {
      const result = await get('/product/list', { categoryId, page: 1, size: 100 });
      this.setData({ products: result.records });
    } catch (e) {
      console.error('加载商品失败', e);
    }
    this.setData({ loading: false });
  },

  onCategoryTap(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ currentIndex: index });
    this.loadProducts(this.data.categories[index].id);
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}` });
  },

  addToCart(e) {
    const product = e.currentTarget.dataset.product;
    cart.addToCart(product);
    wx.showToast({ title: '已加入购物车', icon: 'success' });
  }
});
