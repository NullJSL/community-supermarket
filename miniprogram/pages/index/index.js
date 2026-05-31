const { get } = require('../../utils/request');
const cart = require('../../utils/cart');

Page({
  data: {
    categories: [],
    products: [],
    currentCategory: null,
    keyword: '',
    page: 1,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadCategories();
    this.loadProducts();
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true });
    this.loadProducts().then(() => wx.stopPullDownRefresh());
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMore();
    }
  },

  async loadCategories() {
    try {
      const categories = await get('/category/list');
      this.setData({ categories });
    } catch (e) {
      console.error('加载分类失败', e);
    }
  },

  async loadProducts() {
    this.setData({ loading: true });
    try {
      const params = { page: this.data.page, size: 20 };
      if (this.data.currentCategory) params.categoryId = this.data.currentCategory;
      if (this.data.keyword) params.keyword = this.data.keyword;

      const result = await get('/product/list', params);
      this.setData({
        products: this.data.page === 1 ? result.records : [...this.data.products, ...result.records],
        hasMore: result.current < result.pages
      });
    } catch (e) {
      console.error('加载商品失败', e);
    }
    this.setData({ loading: false });
  },

  loadMore() {
    this.setData({ page: this.data.page + 1 });
    this.loadProducts();
  },

  onCategoryTap(e) {
    const categoryId = e.currentTarget.dataset.id;
    this.setData({
      currentCategory: categoryId === this.data.currentCategory ? null : categoryId,
      page: 1,
      products: []
    });
    this.loadProducts();
  },

  onSearch(e) {
    this.setData({ keyword: e.detail.value, page: 1, products: [] });
    this.loadProducts();
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
