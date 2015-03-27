package sk42.ru.mt;

import android.util.Log;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


public class SoapRequests {

    /**
     * Created with IntelliJ IDEA.
     * User: Sashen
     * Date: 12/17/13
     * Time: 10:58 AM
     * To change this template use File | Settings | File Templates.
     */

        private static final boolean DEBUG_SOAP_REQUEST_RESPONSE = true;
        private static final String MAIN_REQUEST_URL = "http://192.168.0.100/UT/ws/DataCollectorExchange";
        private static final String NAMESPACE = "http://www.sk42.ru";
        private static final String SOAP_ACTION_FindProduct = "http://www.sk42.ru#DataCollectorExchange:FindProductByBarcode";
        private static final String SOAP_ACTION_CreateDoc   = "http://www.sk42.ru#DataCollectorExchange:CreateDoc";

        private SoapObject soapobject;

        private void testHttpResponse(HttpTransportSE ht) {
            ht.debug = DEBUG_SOAP_REQUEST_RESPONSE;
            if (DEBUG_SOAP_REQUEST_RESPONSE) {
                Log.wtf("SOAP RETURN", "Request XML:\n" + ht.requestDump);
                Log.wtf("SOAP RETURN", "\n\n\nResponse XML:\n" + ht.responseDump);
            }
        }


    public SoapObject saveOrder1C() {
        Log.wtf("SoapRequests","saveOrder1C started");
        String methodname = "CreateDoc";
        final String source = "saveOrder1C";

//        Функция CreateDoc(КодУстройства, НомерДокНаУстройстве, НомерДок, СтрокаТовары)
        SoapObject request = new SoapObject(NAMESPACE, methodname);
        request.addProperty("Параметр1", Model.getDeviceCode());
        request.addProperty("Параметр2", Model.getCurrentOrder().index);
        request.addProperty("Параметр3", Model.getCurrentOrder().number);
        request.addProperty("Параметр4", Model.serializeOrderProductsToString());



        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);

        HttpTransportSE ht = getHttpTransportSE();
        try {
            List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
            headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode(Model.getLoginString().getBytes())));
            Log.wtf(source,"exec ht.call");
            ht.call(SOAP_ACTION_CreateDoc, envelope, headerList);
            Log.wtf(source,"ht.call done, exec testHttpResponse");
            testHttpResponse(ht);
            Log.wtf(source,"got Response, exec envelope.getResponse()");
            soapobject = (SoapObject)envelope.getResponse();
            Log.wtf(source,"got soapobject");

        } catch (SocketTimeoutException t) {
            Model.setQueryAnswered();
            Log.e(source,t.toString());
            t.printStackTrace();
            MyApp.showError(source, t.toString());
            return null;
        } catch (IOException i) {
            Model.setQueryAnswered();
            Model.setIOError(true);
            Log.e(source,i.toString());
            i.printStackTrace();
            //MyApp.getInstance().showError(source, i.toString());
            return null;
        } catch (Exception q) {
            Model.setQueryAnswered();
            Log.e(source,q.toString());
            q.printStackTrace();
            MyApp.showError(source, q.toString());
            return null;
        }
        return  soapobject;
    }


    public SoapObject findBarcode(String barcode) {
            final String source = "findBarcode";
            Log.wtf(source,"start");
            Model.clearErrors();
            String methodname = "FindProductByBarcode";


            SoapObject request = new SoapObject(NAMESPACE, methodname);
            Log.wtf(source,"SoapObject created");
            request.addProperty("Barcode", barcode);

            SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
            Log.wtf(source,"Envelope serialized");

            HttpTransportSE httpTransportSE = getHttpTransportSE();
            try {
                List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
                headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode(Model.getLoginString().getBytes())));
                Log.wtf(source, "exec ht.call");
                httpTransportSE.call(SOAP_ACTION_FindProduct, envelope, headerList);
                Log.wtf(source, "exec testHttpResponse");
                testHttpResponse(httpTransportSE);
                soapobject = (SoapObject)envelope.getResponse();
                Log.wtf(source,"got SoapObject");

                /*
                Log.wtf(source,"close connection");
                headerList = new ArrayList<HeaderProperty>();
                headerList.add(new HeaderProperty("Connection", "close"));
                httpTransportSE.call(SOAP_ACTION_FindProduct, envelope, headerList);
                */

            } catch (SocketTimeoutException t) {
                /*Model.setQueryAnswered();*/
                Log.e(source, t.toString());
                t.printStackTrace();
                MyApp.showError(source, t.toString());
                return null;
            } catch (IOException i) {
                //Model.setQueryAnswered();
                Model.setIOError(true);
                if(envelope.bodyIn != null) {
                    String text = envelope.bodyIn.toString();
                    MyApp.notifyNegative(text);
                }
                Log.e(source, i.toString());
                i.printStackTrace();
                //MyApp.getInstance().showError(source, i.toString());
                return null;
            } catch (Exception q) {
                //Model.setQueryAnswered();
                Log.e(source, q.toString());
                q.printStackTrace();
                MyApp.showError(source, q.toString());
                return null;
            }
            return  soapobject;
        }

        private  SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setAddAdornments(false);
            envelope.setOutputSoapObject(request);
            return envelope;
        }


        private  HttpTransportSE getHttpTransportSE() {

            HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,MAIN_REQUEST_URL,60000);
            ht.debug = true;
            ht.setXmlVersionTag("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>");
            return ht;
        }




}

