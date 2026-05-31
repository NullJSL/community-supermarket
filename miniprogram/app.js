App({
  globalData: {
    baseUrl: 'http://localhost:8080/api',
    token: '',
    userInfo: null
  },

  onLaunch() {
    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      this.getUserInfo();
    }
  },

  getUserInfo() {
    const that = this;
    wx.request({
      url: `${this.globalData.baseUrl}/user/info`,
      header: { 'Authorization': `Bearer ${this.globalData.token}` },
      success(res) {
        if (res.data.code === 200) {
          that.globalData.userInfo = res.data.data;
        }
      }
    });
  },

  setToken(token) {
    this.globalData.token = token;
    wx.setStorageSync('token', token);
  },

  logout() {
    this.globalData.token = '';
    this.globalData.userInfo = null;
    wx.removeStorageSync('token');
  }
});
