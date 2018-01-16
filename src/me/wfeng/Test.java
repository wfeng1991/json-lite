package me.wfeng;

public class Test {

    public static void main(String[] args) throws Exception {
//        JSONObject jsonObject = (JSONObject)JSON.parse("{\"avatar_url_template\": \"https://pic4.zhimg.com/238265be6_{size}.jpg\", \"uid\": 55834633568256, \"follow_notifications_count\": 0, \"user_type\": \"people\", \"editor_info\": [], \"headline\": \"java\\u5f00\\u53d1\\u5de5\\u7a0b\\u5e08\", \"default_notifications_count\": 33, \"url_token\": \"wang-feng-30-89\", \"id\": \"b045eb5d54a0320d24af1a090ea13425\", \"messages_count\": 0, \"type\": \"people\", \"name\": \"\\u9ed8\\u5ff5\", \"url\": \"http://www.zhihu.com/api/v4/people/b045eb5d54a0320d24af1a090ea13425\", \"gender\": 1, \"is_advertiser\": false, \"avatar_url\": \"https://pic4.zhimg.com/238265be6_is.jpg\", \"following_question_count\": -400, \"is_org\": false, \"badge\": [], \"vote_thank_notifications_count\": 0}");
//        System.out.println(jsonObject);
//        Object o=JSON.parse("{{'a':12}:1.23,'b':[1.1,2,{'c':121,'d':null},{'e':false}]}");
//        System.out.println(o);
//        String json="{'map':{{'a':'a','b':'a','c':'a','d':'a'}:2132},'count':12331,'booleans':[false,true],'list':[[{'map':{{'a':'a','b':'a','c':'a','d':'a'}:32132},'count':12331,'booleans':[false,true],'list':[]},{'map':{'a':'a','b':'a','c':'a','d':'a'},'count':12331,'booleans':[false,true],'list':[]}]]}";
//        TestBean t = JSONDeserializer.fromJson(json,TestBean.class);
//        System.out.println(t);
//        System.out.println(JSONSerializer.serialize(t));
//        System.out.println(JSONDeserializer.fromJson(JSONSerializer.serialize(t),TestBean.class));
        String jjj="{map:{{'a':'a','b':'a','c':'a','d':'a'}:2132},count:12331,list:[[{'map':{{'a':'a','b':'a','c':'a','d':'a'}:32132},'count':12331,'booleans':[false,true],'list':]},{'map':{'a':'a','b':'a','c':'a','d':'a'},'count':12331,'booleans':[false,true],'list':]}]],booleans:[false,true]}";
        System.out.println(JSON.parse(jjj));
    }
}
