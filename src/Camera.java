import java.util.Random;


public class Camera {

	float FP[];
	float LAP[];
	float VUP[];
	float FL;
	float U[];
	float V[];
	float NGlobal[];
	mathematics m=new mathematics();
	Random rand=new Random();
	public Camera(float[] F, float[] L, float[] Vin, float Fin){
		FP=F;
		LAP=L;
		VUP=Vin;
		FL=Fin;
		this.calculate();
	}
	
	
	
	
	public void calculate(){
		float[] N=new float[3];
		for(int i=0;i<3;i++){
			N[i]=LAP[i]-FP[i];
		}
		NGlobal=N;
		m.normalize(N);
		U=m.vectorCross(VUP, N);
		U=m.normalize(U);
		V=m.vectorCross(N, U);
		
	}
	
	public float[] getIntersectionPoint(float n,float r,float c){
		float[] Floc=FP;
		float[] Nloc=m.vectorScale(NGlobal, FL);
		float[] Uloc=m.vectorScale(U, r);
		float[] Vloc=m.vectorScale(V, c);
		float[] L=m.vectorAdd(Floc, Nloc);
		L=m.vectorAdd(L, Uloc);
		L=m.vectorAdd(L, Vloc);
		float[] Uu=m.vectorSubtract(L, FP);
		Uu=m.normalize(Uu);
		float[] ret=m.vectorAdd(L, m.vectorScale(Uu, n));
		return ret;
	}
	
	public float[] getVVect(float r, float c){
		float[] Floc=FP;
		float[] Nloc=m.vectorScale(NGlobal, FL);
		float[] Uloc=m.vectorScale(U, r);
		float[] Vloc=m.vectorScale(V, c);
		float[] L=m.vectorAdd(Floc, Nloc);
		L=m.vectorAdd(L, Uloc);
		L=m.vectorAdd(L, Vloc);
		float[] Uu=m.vectorSubtract(L, FP);
		Uu=m.normalize(Uu);
		Uu=m.vectorScale(Uu, -1);
		return Uu;
	}
	

	
	

	
	public float[] doesIntersect(Model mod, int r, int c){
		float rloc=mod.getRadius();
		float randr=rand.nextFloat();
		float randc=rand.nextFloat();
		float rnew=r+(randr/2);
		float cnew=c+(randc/2);
		//System.out.println(r+","+c);
		//System.out.println(rnew+","+cnew+"\n");
		float[] Floc=FP;
		float[] Nloc=m.vectorScale(NGlobal, FL);
		float[] Uloc=m.vectorScale(U, rnew);
		float[] Vloc=m.vectorScale(V, cnew);
		float[] L=m.vectorAdd(Floc, Nloc);
		L=m.vectorAdd(L, Uloc);
		L=m.vectorAdd(L, Vloc);
		float[] Uu=m.vectorSubtract(L, FP);
		Uu=m.normalize(Uu);
		float[] pass={0,0,0};
		float[] E=m.vectorSubtract(pass, FP);
		float v=0;
		for(int i=0;i<3;i++){
			v+=E[i]*Uu[i];
		}
		float cloc=m.distance(FP, mod.getCenter());
		float[] ret={-1,0,0};
		if((rloc*rloc)>((cloc*cloc)-(v*v))){
			int[][] tempf=mod.getFaces();
			float[][] tempv=mod.getPoints();
			
			for(int i=0;i<mod.getFaces()[0].length;i++){
				float[][] mat=new float[3][3];
				float[][] vect=new float[1][3];
				vect[0][0]=L[0]-tempv[0][tempf[0][i]];
				vect[0][1]=L[1]-tempv[1][tempf[0][i]];
				vect[0][2]=L[2]-tempv[2][tempf[0][i]];
				mat[0][0]=tempv[0][tempf[1][i]]-tempv[0][tempf[0][i]];
				mat[0][1]=tempv[1][tempf[1][i]]-tempv[1][tempf[0][i]];
				mat[0][2]=tempv[2][tempf[1][i]]-tempv[2][tempf[0][i]];
				mat[1][0]=tempv[0][tempf[2][i]]-tempv[0][tempf[0][i]];
				mat[1][1]=tempv[1][tempf[2][i]]-tempv[1][tempf[0][i]];
				mat[1][2]=tempv[2][tempf[2][i]]-tempv[2][tempf[0][i]];
				mat[2][0]=Uu[0]*-1;
				mat[2][1]=Uu[1]*-1;
				mat[2][2]=Uu[2]*-1;
				float[][] matinv=m.invertMatrix(mat);
				float[][] fvect=m.matrixMultiply(matinv, vect);
				if(fvect[0][0]>=0&&fvect[0][1]>=0&&(fvect[0][0]+fvect[0][1])<=1&&fvect[0][2]>0){
					if(ret[0]==-1){
						ret[0]=0;
						ret[1]=i;
						ret[2]=fvect[0][2];
					}else{
						if(fvect[0][2]<ret[2]){
							ret[0]=0;
							ret[1]=i;
							ret[2]=fvect[0][2];
						}
					}
				}
			}
		}
		return ret;
	}
	

	
}
