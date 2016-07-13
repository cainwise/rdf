package com.yoya;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestSeal {
	
   /**
 * @param x ӡ�µ�x����λ��
 * @param y ӡ�µ�y����λ��
 * @param border ӡ�µı߽��ߺ��
 * @param width ӡ�µĿ��
 * @param height ӡ�µĸ߶�
 * @param text ӡ���ϻ���չʾ���ı�
 * @param cocangle ��Բӡ���ϻ���չʾ���ı�ͷβ���������
 * @param textSize ���ֵĴ�С
 * @param srcImgPath ԴͼƬ·��
 * @param outImgPath ����ͼƬ��·��
 * @throws IOException 
 */
public void mark(int x,int y,int border,int width,int height, String text,int cocangle,
		   int textSize,String srcImgPath, String outImgPath) throws IOException {
	   
	   //��ȡͼƬ�������ͼƬ�����png��ʽ���и��õ��Ӿ�Ч��
       File srcImgFile = new File(srcImgPath);
       Image srcImg = ImageIO.read(srcImgFile);
       int srcImgWidth = srcImg.getWidth(null);
       int srcImgHeight = srcImg.getHeight(null);
       
       BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
       Graphics2D g = bufImg.createGraphics();
       //ȥ���ı�����Բ�ı�Ե��ݸ�
       g.setRenderingHint(
               RenderingHints.KEY_TEXT_ANTIALIASING, 
               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
       g.setRenderingHint(
       		RenderingHints.KEY_ANTIALIASING,
       		RenderingHints.VALUE_ANTIALIAS_ON);
       g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
       //�������壬��ɫ���壬�˴��ɸ���
       Font font = new Font("����", Font.PLAIN, textSize);
       g.setColor(Color.red); 
       g.setFont(font);
       //����Բ
       Ellipse2D el = new Ellipse2D.Double(x,y,width,height);
       g.setStroke(new BasicStroke((float) border));
       g.draw(el);
       //����ԭʼ�ı任
       AffineTransform origin = g.getTransform(); 
       //��һ������Բ������Բ��ԭʼ����ԲСһȦ��
       //ӡ�µ�����д������Բ�ϵģ�������Բ�ᴩÿ���ֵ����ġ�
       //��������ʽ��ʼ���㣬��¼��ʱ��
       System.out.println("a"+System.currentTimeMillis());
       //margin����˼�������϶�����Բ���ϵľ��룬Ҳ������һС������
       double margin = height/15;
       //sub_width����˼������Բ������ĳ��ȣ�sub_height������Բ�̰���ĳ���
       double sub_width = (width/2 - margin - textSize/2)*0.98;
       double sub_height = height/2 - margin - textSize/2;  
       
       /*****************************************
        * �������Ҫ����һ���㷨��
        * 
        * �㷨ʵ�ֵ�Ŀ���Ǳ�֤�������㣺
        * 1.�ѻ����������ֵ�����ƽ�֣���¼����ƽ�ֻ����ĵ㣬��Щ�㶼������Բ�ϣ�����Щ���Ϊ�����ֵ����ġ�
        * 2.����Щ�������Բ�����ߣ���������תÿ���֣�ʹ��ÿ���ֶ���ֱ�����ߡ�
        * 
        * Ϊ�˼�����Բ�Ļ���������΢���־������ֱ������˼�룺
        * 1.����Բ�ֳ�������Σ�ÿһ�ζ�����ֱ�ߣ���ֱ�ߴ��������㻡��
        * 2.�ֵĶ���Խ�࣬����ļ���Խ��ȷ��Ҳ��Խ�ӽ�����Բ�Ļ����ľ�ȷֵ
        * ���ַ���������ֵ���֡�
        * ֮���Բ�����ֵ���ֵķ���������ֱ�Ӷ�ԭ�������л���
        * ����Ϊ��Բ���ֵ�ԭ�������ǳ��Ⱥ������޷�������ֻ�ܲ�����ֵ������
        * 
        * Ϊ�˼������е����ߣ�ͬ��������ֱ������˼�룺
        * 1.�ҵ��е㸽���������㣬һǰһ��
        * 2.������������һ��ֱ�ߣ�ֱ�ߵ�б�ʾ������ߵ�б�ʡ�
        *****************************************/
       
	   //���þ��ȣ�����10000�Σ��������ĵ��������£����Ա�֤���㻡�������ʱ��ȷ��С�������λ
	   int max = 10000;
       
       //rad�ǻ�������Ӧ�ĽǶȣ�step�ǵ�λ������start����ʼ�ĽǶ�
       double rad = Math.toRadians(360 - cocangle);
       double step = rad/max;
       double start = (Math.PI + rad)/2;
       
       //����λ�����飬���У�
       //x0s��¼��ÿ���ֵ����ĵ�x����
       //y0s��¼��ÿ���ֵ����ĵ�y����
       //rots��¼��ÿ���ֵ���ת�Ƕ�
       double x0s[] = new double[text.length()];
       double y0s[] = new double[text.length()];
       double rots[] = new double[text.length()];
       
	   //�������м�����������ս��û��ֱ������       
       double core[] = new double[text.length()];
	   int words_count = text.length();
	   double[] length = new double[max];
	   double[] x_step = new double[max];
	   double[] y_step = new double[max];
	   double[] angles = new double[max];
	   
	   //�ȵ������㻡��
	   double length_total = 0 ;
	   x_step[0] =  Math.cos(start)*sub_width + x + width/2;
       y_step[0] =  -Math.sin(start)*sub_height + y + height/2;
       length[0] =  0;
       angles[0] = start;
	   for (int i = 1; i < max; i++)
	   {
		   angles[i] = start - i*step;
		   x_step[i] =  Math.cos(angles[i])*sub_width + x + width/2;
	       y_step[i] =  -Math.sin(angles[i])*sub_height + y + height/2;  
	       length[i] =  Math.sqrt((x_step[i]-x_step[i-1])*(x_step[i]-x_step[i-1]) +
	    		        (y_step[i]-y_step[i-1])*(y_step[i]-y_step[i-1]));
	       length_total = length_total + length[i-1];
	   }
	   
	   //����λ��
	   double length_avg = length_total/(words_count-1);
	   double length_sum = 0;
	   int j = 1;
	   //������ʼ���λ��
	   core[0] = (Math.PI + rad)/2;	   
	   x0s[0] = Math.cos(core[0])*sub_width + x + width/2;
	   y0s[0] = -Math.sin(core[0])*sub_height + y + height/2;
	   //�����յ��λ��
	   core[words_count-1] = ((Math.PI + rad)/2 - rad*1.01);	   
	   x0s[words_count-1] = Math.cos(core[words_count-1])*sub_width + x + width/2;
	   y0s[words_count-1] = -Math.sin(core[words_count-1])*sub_height + y + height/2;
	   //�����������λ��
	   for(int i = 1; i < max; i++)
	   {
		   length_sum = length_sum + length[i];
		   if(length_sum>(length_avg))
		   {
			   length_sum = 0;
			   core[j] = angles[i]-rad*0.01/words_count*j;
			   x0s[j] = x_step[i];
			   y0s[j] = y_step[i];
			   j=j+1;
			   if(j == words_count)
				   break;
		   }
	   }
       
       //�����������ת�Ƕ�
       for (int i = 0; i < words_count; i++)
       {
    	   	double x_point_before = Math.cos(core[i]+0.01)*sub_width + x + width/2;
    	   	double x_point_after = Math.cos(core[i]-0.01)*sub_width + x + width/2;
    	   	double y_point_before = -Math.sin(core[i]+0.01)*sub_height + y + height/2;
    	   	double y_point_after = -Math.sin(core[i]-0.01)*sub_height + y + height/2;
    	   	double slope = (y_point_after - y_point_before)/(x_point_after - x_point_before);
    	   	rots[i] = 2*Math.PI + Math.atan(slope);
    	   	if(Math.sin(core[i])<0)
    	   	{
    	   		rots[i]=rots[i]+Math.PI;
    	   	}
       }
       //������������ˣ��ټ�¼һ��ʱ��
       System.out.println("a"+System.currentTimeMillis());
       
       //��ӡ���֣�ֵ��ע����ǣ�java�ڴ�ӡ���ֵ�ʱ�򣬲��Ǵ����½ǿ�ʼ��ӡ��
       //��������ƫ�������½ǣ����textSize*0.15����Ϊ�������������
       //���⻹Ҫ�����ֱ���Ĵ�С��textSize/2����Ϊ�������������
       for (int i = 0; i < words_count; i++)
       {
    	   //x0�Ǻ����꣬y0�������꣬rot����ת�Ƕ� 
    	    g.setTransform(origin);
    	    g.rotate(rots[i], x0s[i], y0s[i]);
            g.scale(0.8,1.2);
    	    g.drawString(text.substring(i, i+1), (int)((x0s[i] - (double)textSize/2)/0.8),
    	    		(int)((y0s[i] + (double)textSize/2 - (double)textSize*0.15)/1.2));    	    
       }
       
       g.dispose();
       // ���ͼƬ
       FileOutputStream outImgStream = new FileOutputStream(outImgPath);
       ImageIO.write(bufImg, "png", outImgStream);
       outImgStream.flush();
       outImgStream.close();
}
	
   public static void main(String[] args) throws Exception {
	//������ѿ����Ƽ����޹�˾ӡ
	System.out.println(	System.currentTimeMillis());
	new TestSeal().mark(100,100,2,220,120,"������ѿ����Ƽ����޹�˾",190,16,"E:/Seal.png", "E:/Seal.png");
	System.out.println(	System.currentTimeMillis());
   }
   
}
