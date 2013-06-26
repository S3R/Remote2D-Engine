package com.remote.remote2d.logic;

import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;

public class ColliderMatrixBox extends Collider {
	
	public Vector2DF pos;
	public Vector2DF dim;
	public Vector2D vec;
	
	private float angle = 0;
	private Vector2DF scale = new Vector2DF(1,1);
	private Vector2DF translate = new Vector2DF(0,0);//used to rotate/scale around something that isn't the origin
	private Matrix matrix;
	private Matrix inverse;
	
	public ColliderMatrixBox(Vector2DF pos, Vector2DF dim)
	{
		this.pos = pos;
		this.dim = dim;
		
		matrix = calculateMatrix();
		inverse = calculateInverseMatrix();
		updateVerts();
	}

	@Override
	public boolean isPointInside(Vector2D vec) {
		this.vec = new Vector2D(Matrix.vertexMultiply(inverse.matrix, new Vector3DF(vec.x-pos.x,vec.y-pos.y,1))).add(new Vector2D(pos));
		if(new Vector2D(pos).getColliderWithDim(new Vector2D(dim)).isPointInside(this.vec))
			return true;
		
		return false;
	}
	
	public Matrix calculateMatrix()
	{
		Matrix matrix = new Matrix();
		angle %= 360;
		
		//Convert to local rot/scale
		//matrix.multiply(Matrix.getTranslationMatrix(new Vector2DF(-pos.x,-pos.y)));
		//Rotate based on given angle
		matrix.multiply(Matrix.getRotMatrix(angle));
		//Scale based on given Vector
		matrix.multiply(Matrix.getScaleMatrix(scale));
		//Convert to global coordinates
		//matrix.multiply(Matrix.getTranslationMatrix(pos));
		
		return matrix;
	}
	
	public Matrix calculateInverseMatrix()
	{
		angle %= 360;
		
		Matrix matrix = new Matrix();
		
		//Convert to local rot/scale
		//matrix.multiply(Matrix.getTranslationMatrix(new Vector2DF(-pos.x,-pos.y)));
		//Rotate based on given angle
		matrix.multiply(Matrix.getRotMatrix(-angle));
		//Scale based on given Vector
		matrix.multiply(Matrix.getScaleMatrix(new Vector2DF(1f/scale.x,1f/scale.y)));
		//Convert to global coordinates
		//matrix.multiply(Matrix.getTranslationMatrix(pos));
		return matrix;
	}
	
	public void printMatrix(Matrix m)
	{
		System.out.println();
		for(int x=0;x<m.matrix.length;x++)
		{
			for(int y=0;y<m.matrix[x].length;y++)
			{
				System.out.print(m.matrix[x][y]+" ");
			}
			System.out.println();
		}
	}
	
	public Vector2DF[] getPoints()
	{
		
		Vector2DF[] r = new Vector2DF[4];
		r[0] = new Vector2DF(Matrix.vertexMultiply(matrix.matrix,new Vector3DF(0,0,1)));
		r[1] = new Vector2DF(Matrix.vertexMultiply(matrix.matrix,new Vector3DF(dim.x,0,1)));
		r[2] = new Vector2DF(Matrix.vertexMultiply(matrix.matrix,new Vector3DF(dim.x,dim.y,1)));
		r[3] = new Vector2DF(Matrix.vertexMultiply(matrix.matrix,new Vector3DF(0,dim.y,1)));
		
		
		return r;
	}
	
	@Override
	public void drawCollider() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glColor3f(0, 1, 0);
		GL11.glTranslatef(pos.x,pos.y,0);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		{
			Vector2DF pos[] = getPoints();
			GL11.glVertex2f(pos[0].x, pos[0].y);
			GL11.glVertex2f(pos[1].x, pos[1].y);
			GL11.glVertex2f(pos[2].x, pos[2].y);
			GL11.glVertex2f(pos[3].x, pos[3].y);
			GL11.glVertex2f(pos[0].x, pos[0].y);
		}
		GL11.glEnd();
		GL11.glTranslatef(-pos.x,-pos.y,0);
		
		GL11.glColor3f(0, 0, 1);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		{
			GL11.glVertex2f(pos.x, pos.y);
			GL11.glVertex2f(pos.x+dim.x, pos.y);
			GL11.glVertex2f(pos.x+dim.x, pos.y+dim.y);
			GL11.glVertex2f(pos.x, pos.y+dim.y);
			GL11.glVertex2f(pos.x, pos.y);
		}
		GL11.glEnd();
		
		if(vec != null)
		{
			GL11.glColor3f(0, 0, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(vec.x-3, vec.y-3);
				GL11.glVertex2f(vec.x+3, vec.y-3);
				GL11.glVertex2f(vec.x+3, vec.y+3);
				GL11.glVertex2f(vec.x-3, vec.y+3);
			GL11.glEnd();
		}
		
		//GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public float getAngle() {
		return angle;
	}

	public ColliderMatrixBox setAngle(float angle) {
		this.angle = angle;
		matrix = calculateMatrix();
		inverse = calculateInverseMatrix();
		return this;
	}

	public Vector2DF getScale() {
		return scale;
	}

	public ColliderMatrixBox setScale(Vector2DF scale) {
		this.scale = scale;
		matrix = calculateMatrix();
		inverse = calculateInverseMatrix();
		return this;
	}

	@Override
	public Collider getTransformedCollider(Vector2D trans) {
		ColliderMatrixBox box = new ColliderMatrixBox(pos.add(new Vector2DF(trans.getElements())),dim);
		box.setAngle(angle);
		box.setScale(scale);
		return box;
	}

	@Override
	public void updateVerts() {
		Vector2DF[] ver = getPoints();
		verts = new Vector2D[4];
		verts[0] = new Vector2D(ver[0]);
		verts[1] = new Vector2D(ver[1]);
		verts[2] = new Vector2D(ver[2]);
		verts[3] = new Vector2D(ver[3]);
	}

}
