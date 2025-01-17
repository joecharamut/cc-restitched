/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2021. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.common;

import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.network.client.TerminalState;
import net.minecraft.nbt.NbtCompound;

public class ClientTerminal implements ITerminal
{
    private boolean colour;
    private Terminal terminal;
    private boolean terminalChanged;

    public ClientTerminal( boolean colour )
    {
        this.colour = colour;
        terminal = null;
        terminalChanged = false;
    }

    public boolean pollTerminalChanged()
    {
        boolean changed = terminalChanged;
        terminalChanged = false;
        return changed;
    }

    // ITerminal implementation

    @Override
    public Terminal getTerminal()
    {
        return terminal;
    }

    @Override
    public boolean isColour()
    {
        return colour;
    }

    public void read( TerminalState state )
    {
        colour = state.colour;
        if( state.hasTerminal() )
        {
            resizeTerminal( state.width, state.height );
            state.apply( terminal );
        }
        else
        {
            deleteTerminal();
        }
    }

    private void resizeTerminal( int width, int height )
    {
        if( terminal == null )
        {
            terminal = new Terminal( width, height, () -> terminalChanged = true );
            terminalChanged = true;
        }
        else
        {
            terminal.resize( width, height );
        }
    }

    private void deleteTerminal()
    {
        if( terminal != null )
        {
            terminal = null;
            terminalChanged = true;
        }
    }

    public void readDescription( NbtCompound nbt )
    {
        colour = nbt.getBoolean( "colour" );
        if( nbt.contains( "terminal" ) )
        {
            NbtCompound terminal = nbt.getCompound( "terminal" );
            resizeTerminal( terminal.getInt( "term_width" ), terminal.getInt( "term_height" ) );
            this.terminal.readFromNBT( terminal );
        }
        else
        {
            deleteTerminal();
        }
    }
}
