package com.furb.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;

import com.furb.pedido.Pedido;

public class SerializationUtils {
	public static byte[] ObjectToByteArray(Serializable xy){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] result = null;
		
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(xy);
			result = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}				
		}
		
		return result;
	}
	
	public static byte[] ObjectArrayToByteArray(Serializable[] xy){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] result = null;
		
		try {
			out = new ObjectOutputStream(bos);
			for (int i = 0; i < xy.length; i++) {
				out.writeObject(xy[1]);
			}
			
			result = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}				
		}
		
		return result;
	}
	
	public static Object ByteArrayToObject(byte[] byteArray){
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
		ObjectInput in = null;
		Object result = null;
		
		try {
		  in = new ObjectInputStream(bis);
		  result = in.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block			
		} finally {
		  try {
		    bis.close();
		    if (in != null) {
			      in.close();
			    }
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		
		return result;
	}
	
	public static byte[] PedidosToArrayByte(Pedido[] pedidos) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		for (Pedido pedido : pedidos) {
			try {
				baos.write(MessageFormat.format("{0};{1};{2}\n",
						pedido.getCodigo(), pedido.getCoordenadaX(),
						pedido.getCoordenadaY()).getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return baos.toByteArray();
	}
	
	public static <T> T[] ByteArrayToArrayObject(byte[] byteArray){
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
		ObjectInput in = null;
		ArrayList<T> arrayOfObject = new ArrayList<T>();
		T parcResult = null;
		
		try {
		  in = new ObjectInputStream(bis);
		  
		  while((parcResult = (T)in.readObject()) != null){
			  arrayOfObject.add(parcResult);
		  }
		  
		} catch (Exception e) {
			// TODO Auto-generated catch block			
		} finally {
		  try {
		    bis.close();
		    if (in != null) {
			      in.close();
			    }
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		
		T[] result = (T[])Array.newInstance(parcResult.getClass(), arrayOfObject.size());
		
		arrayOfObject.toArray(result);
		
		return result;
	}
}
