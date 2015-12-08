
public class Model {

	private float vertex[][];
	private int faces[][];
	private float faceprops[][];
	private float cp[];
	private float radius;
	mathematics m= new mathematics();
	
	public Model(float vert[][], int face[][]){
		vertex=vert;
		int[][] temp;
		if(face.length>3){
			temp=new int[3][face[0].length*(face.length-2)];
			for(int i=0;i<face[0].length;i++){
				for(int j=0;j<(face.length-2);j++){
					temp[0][i*(face.length-2)+j]=face[0][i];
					temp[1][i*(face.length-2)+j]=face[j+1][i];
					temp[2][i*(face.length-2)+j]=face[j+2][i];
				}
			}
			faces=temp;
		}
		else faces=face;
		this.initFaceProps();
		this.calculateCenter();
		this.calcutateRadius();
	}
	
	public void printVertex(){
		for(int i=0; i<vertex[0].length; i++){
			for(int j=0; j<vertex.length; j++){
				System.out.print(vertex[j][i]+" ");
			}
			System.out.println();
		}
	}
	
	public void initFaceProps(){
		faceprops=new float[5][faces[0].length];
		for(int i=0;i<faces[0].length;i++){
			faceprops[0][i]=(float)0.5;
			faceprops[1][i]=(float)0.5;
			faceprops[2][i]=(float)0.5;
			faceprops[3][i]=0;
			faceprops[4][i]=1;
		}
	}
	
	public void setFaceProps(int low, int high, float[] lamb, float rfc, float shine){
		for(int i=low;i<(high+1);i++){
			faceprops[0][i]=lamb[0];
			faceprops[1][i]=lamb[1];
			faceprops[2][i]=lamb[2];
			faceprops[3][i]=rfc;
			faceprops[4][i]=shine;
		}
	}
	
	public void printFaceProps(){
		m.printMatrix(faceprops);
	}
	
	public float[] ambientCalc(int face, float[] ambientVal){
		float[][] k=new float[3][3];
		float[][] B=new float[1][3];
		float[] ret=new float[3];
		for(int i=0; i<3;i++){
			k[i][i]=faceprops[i][face];
			B[0][i]=ambientVal[i];
		}
		float[][] temp=m.matrixMultiply(k, B);
		for(int i=0;i<3;i++){
			ret[i]=temp[0][i];
		}
		return ret;
	}
	
	public float[] diffuseCalc(int face, float[] Light, float[] intersectionPoint){
		float[][] k=new float[3][3];
		float[][] B=new float[1][3];
		float[] ret=new float[3];
		for(int i=0; i<3;i++){
			k[i][i]=faceprops[i][face];
			B[0][i]=Light[i+3];
		}
		float[] N=this.getSurfaceNormal(face);
		float[] lightPos= new float[3];
		for(int i=0;i<3;i++){
			lightPos[i]=Light[i];
		}
		float[] L=m.vectorSubtract(intersectionPoint, lightPos);
		L=m.normalize(L);
		float mult=m.distance(N, L);
		float[][] temp=m.matrixMultiply(k, B);
		for(int i=0;i<3;i++){
			ret[i]=temp[0][i];
		}
		return m.vectorScale(ret, mult);
		
	}
	
	public float[] specularCalc(int face, float[] Light, float[] intersectionPoint, float[] V,float k,float con){
		float[] B=new float[3];
		for(int i=0; i<3;i++){
			B[i]=Light[i+3];
		}
		B=m.vectorScale(B, con);
		float[] N=this.getSurfaceNormal(face);
		float[] lightPos= new float[3];
		for(int i=0;i<3;i++){
			lightPos[i]=Light[i];
		}
		float[] L=m.vectorSubtract(intersectionPoint, lightPos);
		L=m.normalize(L);
		float mult=m.distance(L, N);
		mult=mult*2;
		float[] R=m.vectorScale(m.vectorAdd(N, m.vectorScale(L, -1)), mult);
		float mult2=m.distance(L, R);
		mult2=(float) Math.pow(mult2, con);
		mult2=mult2*k;
		return m.vectorScale(B, mult2);
	}
	
	public float[] getSurfaceNormal(int face){
		float[] p1=new float[3];
		float[] p2=new float[3];
		float[] p3=new float[3];
		for(int i=0;i<3;i++){
			p1[i]=vertex[i][faces[0][face]];
			p2[i]=vertex[i][faces[1][face]];
			p3[i]=vertex[i][faces[2][face]];
		}
		return m.vectorCross(m.vectorSubtract(p2, p1), m.vectorSubtract(p3, p1));
	}
	
	
	public void printFaces(){
		for(int i=0; i<faces[0].length; i++){
			for(int j=0; j<faces.length; j++){
				System.out.print(faces[j][i]+" ");
			}
			System.out.println();
		}
	}
	
	public void calculateCenter(){
		cp=new float[3];
		cp[0]=0;
		cp[1]=0;
		cp[2]=0;
		for(int i=0;i<vertex[0].length;i++){
			cp[0]=cp[0]+vertex[0][i];
			cp[1]=cp[1]+vertex[1][i];
			cp[2]=cp[2]+vertex[2][i];
		}
		cp[0]=cp[0]/vertex[0].length;
		cp[1]=cp[1]/vertex[0].length;
		cp[2]=cp[2]/vertex[0].length;
	}
	
	public void calcutateRadius(){
		float[] outter=new float[3];
		outter[0]=vertex[0][0];
		outter[1]=vertex[1][0];
		outter[2]=vertex[2][0];
		for(int i=1; i<vertex[0].length;i++){
			if(vertex[0][i]<outter[0])outter[0]=vertex[0][i];
			if(vertex[1][i]<outter[1])outter[1]=vertex[1][i];
			if(vertex[2][i]<outter[2])outter[2]=vertex[2][i];
		}
		radius=m.distance(outter, cp);
	}
	
	public float getRadius(){
		return radius;
	}
	
	public float[] getCenter(){
		return cp;
	}
	
	public int[][] getFaces(){
		return faces;
	}
	
	public float[][] getPoints(){
		return vertex;
	}
	
}
