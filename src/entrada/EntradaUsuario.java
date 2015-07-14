package entrada;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class EntradaUsuario implements KeyListener, FocusListener, MouseListener, MouseMotionListener
{
    public boolean[] tecla = new boolean[68836];
    public EntradaUsuario() 
    {
        for(int i=0;i<tecla.length;i++)
            tecla[i] = false;
    }
    @Override
    public void keyPressed(KeyEvent e)
    {
        // TODO Auto-generated method stub
        int codigo = e.getKeyCode();
        if (codigo > 0 & codigo < tecla.length)
        {
            tecla[codigo] = true;
            //System.out.println("Presion"+codigo);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        // TODO Auto-generated method stub
        /*for(int codigo =0; codigo<tecla.length;codigo++)
            tecla[codigo] = false;*/
        int codigo = e.getKeyCode();
        if (codigo > 0 & codigo < tecla.length)
        {
            tecla[codigo] = false;
            //System.out.println("Solto"+codigo);
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseDragged(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseMoved(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseClicked(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void focusGained(FocusEvent arg0)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void focusLost(FocusEvent arg0)
    {
        // TODO Auto-generated method stub
        for(int i=0;i<tecla.length;i++)
            tecla[i] = false;
    }

}
