package com.xdtech.project.lot.mjj.message.struct;

public abstract class Struct {

    public abstract void load(byte[] data);

    public abstract byte[] toBytes();

    @Override
    public String toString() {
        byte[] data = toBytes();

        StringBuffer out = new StringBuffer();

        for (int i = 0; i < data.length; i++) {

            if (i != 0 && i % 32 == 0) {
                out.append("\n");
            }

            out.append(" ");
            String d = Integer.toHexString(data[i] & 0x000000ff);
            if (d.length() == 1) {
                out.append("0" + d);
            } else {
                out.append(d);
            }

        }

        return out.toString();
    }

//	protected StructFieldDescriptor[] fields;
//	
//	public void load(byte[] data){
//		
//	}
//	
//	public boolean isDynamic(){
//		return false;
//	}
//	
//	public String getLengthField(){
//		return null;
//	}
//	
//	public int sizeof(){
//		return 0;
//	}	
//	
//	protected StructFieldDescriptor[] getFields(){
//		
//		if(fields == null){
//			Stack<Field> stack = new Stack<Field>();
//			Class<?> clazz = this.getClass();
//			
//			try{
//				while(Struct.class.isAssignableFrom(clazz)){
//					Field[] fields = clazz.getDeclaredFields();
//					Field[] cache = new Field[fields.length];
//					
//					for(int i = 0;i < fields.length; i++){
//						cache[fields.length - i - 1] = fields[i];
//					}
//					
//					for(int i = 0; i < cache.length; i++){
//						stack.add(cache[i]);
//					}
//					
//					clazz = clazz.getSuperclass();
//				}
//				
//				fields = new StructFieldDescriptor[stack.size()];
//				int i = 0;
//				int offset = 0;
//				
//				while(!stack.isEmpty()){
//					Field f = stack.pop();
//					if(Type.class.isAssignableFrom(f.getType())){
//						int sizeof = Type.SIZEOF(f.getType());
//						fields[i++] = new StructFieldDescriptor(f,offset,sizeof);
//						offset += sizeof;
//					}
//					
//				}
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		return fields;
//	}

}
