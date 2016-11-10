##一、Volley的基本请求：

####      1.构建请求
````java
 StringRequest stringRequest = new StringRequest(Request.Method.POST,url,listener,errorListner){
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> map = new HashMap<String, String>();
        map.put("start", "1");
        map.put("count", "5");
        return map;
    }
}
````
####       2.加入队列（基本请求队列）
````java
stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
VolleyTool.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
````
     用到的工具类：VolleyTool.java:

#####       3.加入队列（Disk缓存请求队列）

````java
VolleyTool.getInstance(this.getApplicationContext(), true).addToRequestQueue(stringRequest);
````
##二、Volley的工作原理：

![image](https://raw.githubusercontent.com/zhaolewei/11.5-Volley/master/img1.png)

其中蓝色部分代表主线程，绿色部分代表缓存线程，橙色部分代表网络线程。
从左上角开始：
(1). 请求按优先级顺序添加到缓存队列中：  
         通过RequestQueue的add方法把请求添加到缓存队列中  
(2).请求被CatchDispatcher找出：找到->(3);未找到->(4) 
(3).读取请求并返回给主线程  
(4).CatchDispatcher缓存中未找到改请求时，再使用NetWorkDispatcher去执行网络请求  
(5).网络请求，并返回给主线程  
