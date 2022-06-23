const proxy = require("http-proxy-middleware")

module.exports = app => {
  app.use('/bfgws', proxy.createProxyMiddleware({target: "http://localhost:8080", ws: true}))
}
