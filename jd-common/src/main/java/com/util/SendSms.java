package com.util;

import java.util.Map;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

public class SendSms {
	public static void sendSms(Map<String, String> map) {
		final String accesskeyId = "LTAIWk2c968a1DPX";
		final String accesskeySecret = "yYBLkx3c95usoGDdwkPpwfWmHvHnad";

		DefaultProfile profile = DefaultProfile.getProfile("default", accesskeyId, accesskeySecret);
		IAcsClient client = new DefaultAcsClient(profile);

		CommonRequest request = new CommonRequest();
		// request.setProtocol(ProtocolType.HTTPS);
		request.setMethod(MethodType.POST);
		request.setDomain("dysmsapi.aliyuncs.com");
		request.setVersion("2017-05-25");
		request.setAction("SendSms");
//		request.putQueryParameter("PhoneNumbers", "15680079609");
//		request.putQueryParameter("SignName", "校云");
//		request.putQueryParameter("TemplateCode", "SMS_165410460");
//		request.putQueryParameter("TemplateParam", "{\"code\":\"8985\"}");

		request.putQueryParameter("PhoneNumbers", map.get("phone"));
		request.putQueryParameter("SignName", map.get("signName"));
		request.putQueryParameter("TemplateCode", map.get("templateCode"));
		request.putQueryParameter("TemplateParam", "{\"code\":'" + map.get("code") + "'}");

		try {
			CommonResponse response = client.getCommonResponse(request);
			System.out.println(response.getData());
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
}