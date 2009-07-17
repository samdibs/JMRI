package jmri;

import jmri.jmrit.beantable.LogixTableAction;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * ConditionalVariable.java
 *
 * The variable used in the antecedent (the 'if' part) of the Conditional. 
 * proposition.  The states of ConditionalVariables and logic expression 
 * of the antecedent determine the state of the Conditional.
 * <P>
 * This file is part of JMRI.
 * <P>
 * JMRI is free software; you can redistribute it and/or modify it under 
 * the terms of version 2 of the GNU General Public License as published 
 * by the Free Software Foundation. See the "COPYING" file for a copy
 * of this license.
 * <P>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 * <P>
 * @author			Pete Cressman Copyright (C) 2009
 * @version			$Revision 1.0 $
 */


public class ConditionalVariable {

	static final ResourceBundle rbx = ResourceBundle
			.getBundle("jmri.jmrit.beantable.LogixTableBundle");

    public static final int NUM_COMPARE_TYPES  = 5;
    public static final int LESS_THAN          = 1;
    public static final int LESS_THAN_OR_EQUAL = 2;
    public static final int EQUAL              = 3;
    public static final int GREATER_THAN_OR_EQUAL = 4;
    public static final int GREATER_THAN       = 5; 

    private boolean _not = false;
    // Not a variable attribute, but retained as an artifact of previous releases.  This will be used
    // as the default operator immediately to the left of this variable in the antecedent statement. 
    // It may be over written by the antecedent statement in the Conditional to which this variable 
    // belongs.
    private int _opern = Conditional.OPERATOR_NONE;
    private int _type   = Conditional.TYPE_NONE;
    private String _name = "";
    private String _dataString = "";
    private int _num1 = 0;
    private int _num2 = 0;
    // Name clarification: Formerly was named '_triggersCalculation' because it controlled whether
    // a listener was installed for this device and thus trigger calcuation of the Conditional.
    // Now named '_triggersActions' because listeners are always installed for activated Logix 
    // Conditionals and this parameter nows controls whether, if its change of state changes the
    // state of the conditional, should that also  trigger the actions.
    private boolean _triggersActions = true;
    private int _state = Conditional.UNKNOWN;        // tri-state

    public ConditionalVariable() {
    }

    public ConditionalVariable(boolean not, int opern, int type, String name, boolean trigger) {
        _not = not;
        _opern = opern;
        _type = type;
        _name = name;
        _triggersActions = trigger;
    }

    public boolean isNegated() {
        return _not;
    }

    public void setNegation(boolean not) {
        _not = not;
    }

    public int getOpern() {
        return _opern;
    }

    public void setOpern(int opern) {
        if (opern == Conditional.OPERATOR_AND_NOT) {
            _opern = Conditional.OPERATOR_AND;
            _not = true;
        } else if (opern == Conditional.OPERATOR_NOT) {
            _opern = Conditional.OPERATOR_NONE;
            _not = true;
        } else {
            _opern = opern;
        }
    }

    public int getType() {
        return _type;
    }

    public void setType(int type) {
        _type = type;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDataString() {
        return _dataString;
    }

    public void setDataString(String data) {
        _dataString = data;
    }

    public int getNum1() {
        return _num1;
    }

    public void setNum1(int num) {
        _num1 = num;
    }

    public int getNum2() {
        return _num2;
    }

    public void setNum2(int num) {
        _num2 = num;
    }

    /**
    * If change of state of this object causes a change of state of the Conditional,
    * should any actions be executed.
    */
    public boolean doTriggerActions() {
        return _triggersActions;
    }

    public void setTriggerActions(boolean trigger) {
        _triggersActions = trigger;
    }

    public int getState() {
        return _state;
    }

    public void setState(int state) {
        _state = state;
    }

    public void setState(boolean state) {
        if (state) {
            _state = Conditional.TRUE;
        }
        else {
            _state = Conditional.FALSE;
        }
    }

    public String getTypeString() {
        return getTypeString(_type);
    }

    public String getOpernString() {
        switch (_opern) {
            case Conditional.OPERATOR_AND:
                return rbx.getString("LogicAND");
            case Conditional.OPERATOR_NOT:
                return rbx.getString("LogicNOT");
            case Conditional.OPERATOR_AND_NOT:	
                return rbx.getString("LogicAND");
            case Conditional.OPERATOR_NONE:
                return "";
            case Conditional.OPERATOR_OR:
                return rbx.getString("LogicOR");
        }
        return "";
    }

    /**
    * override Object methods to avoid redundancy in HashSet
    *
    public boolean equals(Object obj) {
        ConditionalVariable v = (ConditionalVariable)obj;
        if (_name.equals(v.getName()) && _type == v.getType() ) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        char[] ch = _name.toCharArray();
        int hCode= 0;
        for (int i=0; i<ch.length; i++)
        {
            hCode += ch[i];
        }
        hCode += _type;
        return hCode;
    }
    */

	/**
	*  Evaluates this State Variable
	*  <P>
	*  Returns true if variable evaluates true, otherwise false. 
	*/
	@SuppressWarnings("deprecation")        // Date.getMinutes, Date.getHours
	public boolean evaluate() {
		boolean result = true;
		Sensor sn = null;
		Turnout t = null;
		SignalHead h = null;
		Conditional c = null;
		Light lgt = null;
		Memory m = null;
		// evaluate according to state variable type		
		switch (_type) {
			case Conditional.TYPE_SENSOR_ACTIVE:
				sn = InstanceManager.sensorManagerInstance().provideSensor(_name);
				if (sn == null) {
					log.error("invalid sensor name= \""+_name+"\" in state variable");
					return (false);
				}
				if (sn.getState() == Sensor.ACTIVE) result = true;
				else result = false;
				break;
			case Conditional.TYPE_SENSOR_INACTIVE:
				sn = InstanceManager.sensorManagerInstance().provideSensor(_name);
				if (sn == null) {
					log.error("invalid sensor name= \""+_name+"\" in state variable");
					return (false);
				}
				if (sn.getState() == Sensor.INACTIVE) result = true;
				else result = false;
				break;
			case Conditional.TYPE_TURNOUT_THROWN:
				t = InstanceManager.turnoutManagerInstance().provideTurnout(_name);
				if (t == null) {
					log.error("invalid turnout name= \""+_name+"\" in state variable");
					return (false);
				}
				if (t.getKnownState() == Turnout.THROWN) result = true;
				else result = false;
				break;
			case Conditional.TYPE_TURNOUT_CLOSED:
				t = InstanceManager.turnoutManagerInstance().provideTurnout(_name);
				if (t == null) {
					log.error("invalid turnout name= \""+_name+"\" in state variable");
					return (false);
				}
				if (t.getKnownState() == Turnout.CLOSED) result = true;
				else result = false;
				break;
			case Conditional.TYPE_CONDITIONAL_TRUE:
				c = InstanceManager.conditionalManagerInstance().getBySystemName(_name);
				if (c ==null) {
                    c = InstanceManager.conditionalManagerInstance().getByUserName(_name);
                    if (c == null) {
                        log.error("invalid conditional name= \""+_name+"\" in state variable");
                        return (false);
                    }
				}
				if (c.getState() == Conditional.TRUE) result = true;
				else result = false;
				break;
			case Conditional.TYPE_CONDITIONAL_FALSE:
				c = InstanceManager.conditionalManagerInstance().getBySystemName(_name);
				if (c ==null) {
                    c = InstanceManager.conditionalManagerInstance().getByUserName(_name);
                    if (c == null) {
                        log.error("invalid conditional name= \""+_name+"\" in state variable");
                        return (false);
                    }
				}
				if (c.getState() == Conditional.FALSE) result = true;
				else result = false;
				break;
			case Conditional.TYPE_LIGHT_ON:
				lgt = InstanceManager.lightManagerInstance().getLight(_name);
				if (lgt == null) {
					log.error("invalid light name= \""+_name+"\" in state variable");
					return (false);
				}
				if (lgt.getState() == Light.ON) result = true;
				else result = false;
				break;
			case Conditional.TYPE_LIGHT_OFF:
				lgt = InstanceManager.lightManagerInstance().getLight(_name);
				if (lgt == null) {
					log.error("invalid light name= \""+_name+"\" in state variable");
					return (false);
				}
				if (lgt.getState() == Light.OFF) result = true;
				else result = false;
				break;
			case Conditional.TYPE_MEMORY_EQUALS:
				m = InstanceManager.memoryManagerInstance().provideMemory(_name);
				if (m == null) {
					log.error("invalid memory name= \""+_name+"\" in state variable");
					return (false);
				}
				String memStr = (String)m.getValue();
                result = compare(memStr,  _dataString);
				break;
			case Conditional.TYPE_MEMORY_COMPARE:
				m = InstanceManager.memoryManagerInstance().provideMemory(_name);
				if (m == null) {
					log.error("invalid memory name= \""+_name+"\" in state variable");
					return (false);
				}
				String value1 = (String)m.getValue();
				Memory m2 = InstanceManager.memoryManagerInstance().provideMemory(_dataString);
				if (m2 == null) {
					log.error("invalid data memory name= \""+_dataString+"\" in state variable");
					return (false);
				}
				String value2 = (String)m2.getValue();
                result = compare(value1,  value2);
				break;
			case Conditional.TYPE_FAST_CLOCK_RANGE:
				// get current fast clock time
				Timebase fastClock = InstanceManager.timebaseInstance();
				Date currentTime = fastClock.getTime();
				int currentMinutes = (currentTime.getHours()*60) + currentTime.getMinutes();
                int beginTime = fixMidnight(_num1);
                int endTime = fixMidnight(_num2);
				// check if current time is within range specified
				if (beginTime<endTime) {
					// range is entirely within one day
					if ( (beginTime<=currentMinutes)&&(currentMinutes<=endTime) ) result = true;
					else result = false;
				}
				else {
					// range includes midnight
					if (beginTime<=currentMinutes || currentMinutes<=endTime) result = true;
					else result = false;
				}
				break;
			case Conditional.TYPE_SIGNAL_HEAD_RED:
				h = InstanceManager.signalHeadManagerInstance().getSignalHead(_name);
				if (h == null) {
					log.error("invalid signal head name= \""+_name+"\" in state variable");
					return (false);
				}
				if (h.getAppearance() == SignalHead.RED) result = true;
				else result = false; 
				break;
			case Conditional.TYPE_SIGNAL_HEAD_YELLOW:
				h = InstanceManager.signalHeadManagerInstance().getSignalHead(_name);
				if (h == null) {
					log.error("invalid signal head name= \""+_name+"\" in state variable");
					return (false);
				}
				if (h.getAppearance() == SignalHead.YELLOW) result = true;
				else result = false; 
				break;
			case Conditional.TYPE_SIGNAL_HEAD_GREEN:
				h = InstanceManager.signalHeadManagerInstance().getSignalHead(_name);
				if (h == null) {
					log.error("invalid signal head name= \""+_name+"\" in state variable");
					return (false);
				}
				if (h.getAppearance() == SignalHead.GREEN) result = true;
				else result = false; 
				break;
			case Conditional.TYPE_SIGNAL_HEAD_DARK:
				h = InstanceManager.signalHeadManagerInstance().getSignalHead(_name);
				if (h == null) {
					log.error("invalid signal head name= \""+_name+"\" in state variable");
					return (false);
				}
				if (h.getAppearance() == SignalHead.DARK) result = true;
				else result = false; 
				break;
			case Conditional.TYPE_SIGNAL_HEAD_FLASHRED:
				h = InstanceManager.signalHeadManagerInstance().getSignalHead(_name);
				if (h == null) {
					log.error("invalid signal head name= \""+_name+"\" in state variable");
					return (false);
				}
				if (h.getAppearance() == SignalHead.FLASHRED) result = true;
				else result = false; 
				break;
			case Conditional.TYPE_SIGNAL_HEAD_FLASHYELLOW:
				h = InstanceManager.signalHeadManagerInstance().getSignalHead(_name);
				if (h == null) {
					log.error("invalid signal head name= \""+_name+"\" in state variable");
					return (false);
				}
				if (h.getAppearance() == SignalHead.FLASHYELLOW) result = true;
				else result = false; 
				break;
			case Conditional.TYPE_SIGNAL_HEAD_FLASHGREEN:
				h = InstanceManager.signalHeadManagerInstance().getSignalHead(_name);
				if (h == null) {
					log.error("invalid signal head name= \""+_name+"\" in state variable");
					return (false);
				}
				if (h.getAppearance() == SignalHead.FLASHGREEN) result = true;
				else result = false; 
				break;
			case Conditional.TYPE_SIGNAL_HEAD_LIT:
				h = InstanceManager.signalHeadManagerInstance().getSignalHead(_name);
				if (h == null) {
					log.error("invalid signal head name= \""+_name+"\" in state variable");
					return (false);
				}
				result = h.getLit(); 
				break;
			case Conditional.TYPE_SIGNAL_HEAD_HELD:
				h = InstanceManager.signalHeadManagerInstance().getSignalHead(_name);
				if (h == null) {
					log.error("invalid signal head name= \""+_name+"\" in state variable");
					return (false);
				}
				result = h.getHeld();
				break;
		}
		// apply NOT if specified
		if ( _not ) {
			result = !result;
		}
        if (result)
            setState(Conditional.TRUE);
        else
            setState(Conditional.FALSE);
		return (result);
	}

    @SuppressWarnings("fallthrough")
    boolean compare(String value1, String value2) {
        if (value1==null) {
            if (value2==null) return true;
        } else {
            if (value2==null) return false;
            value1 = value1.trim();
            value2 = value2.trim();
        }
        //
        try {
            int n1 = Integer.parseInt(value1);
            try {
                int n2 = Integer.parseInt(value2);
                if (_num1 == 0) { // for former code
                    if (n1 == n2) return true;
                    else return false;
                }
                if (log.isDebugEnabled()) log.debug("Compare numbers: n1= "+n1+", to n2= "+n2);
                switch (_num1)  // both are numbers
                {   // fall through
                    case LESS_THAN:
                        if (n1 < n2) return true;
                    case LESS_THAN_OR_EQUAL:
                        if (n1 <= n2) return true;
                    case EQUAL:
                        if (n1 == n2) return true;
                    case GREATER_THAN_OR_EQUAL:
                        if (n1 >= n2) return true;
                    case GREATER_THAN:
                        if (n1 > n2) return true;
                }
            } catch (NumberFormatException nfe) {
                return false;   // n1 is a number, n2 is not
            }
            log.error("Compare 'numbers': value1= "+value1+", to value2= "+value2);
        } catch (NumberFormatException nfe) { 
            try {
                Integer.parseInt(value2);
                return false;     // n1 is not a number, n2 is
            } catch (NumberFormatException ex) { // OK neither a number
            }
        }
        log.debug("Compare Strings: value1= "+value1+", to value2= "+value2);
        int compare = value1.compareTo(value2);
        if (_num1 == 0) { // for former code
            if (compare == 0) return true;
            else return false;
        }
        switch (_num1)
        {   // fall through
            case LESS_THAN:
                if (compare < 0) return true;
            case LESS_THAN_OR_EQUAL:
                if (compare <= 0) return true;
            case EQUAL:
                if (compare == 0) return true;
            case GREATER_THAN_OR_EQUAL:
                if (compare >= 0) return true;
            case GREATER_THAN:
                if (compare > 0) return true;
        }
        return false;
    }

    private int fixMidnight(int time) {
        if (time>24*60)
            time -= 24*60;
        return time;
    }

	/**
	 * Convert Variable Type to Text String
	 */
	public static String getTypeString(int t) {
		switch (t) {
            case Conditional.TYPE_NONE:
                return "";
            case Conditional.TYPE_SENSOR_ACTIVE:
                return (rbx.getString("TypeSensorActive"));
            case Conditional.TYPE_SENSOR_INACTIVE:
                return (rbx.getString("TypeSensorInactive"));
            case Conditional.TYPE_TURNOUT_THROWN:
                return (rbx.getString("TypeTurnoutThrown"));
            case Conditional.TYPE_TURNOUT_CLOSED:
                return (rbx.getString("TypeTurnoutClosed"));
            case Conditional.TYPE_CONDITIONAL_TRUE:
                return (rbx.getString("TypeConditionalTrue"));
            case Conditional.TYPE_CONDITIONAL_FALSE:
                return (rbx.getString("TypeConditionalFalse"));
            case Conditional.TYPE_LIGHT_ON:
                return (rbx.getString("TypeLightOn"));
            case Conditional.TYPE_LIGHT_OFF:
                return (rbx.getString("TypeLightOff"));
            case Conditional.TYPE_MEMORY_EQUALS:
                return (rbx.getString("TypeMemoryEquals"));
            case Conditional.TYPE_MEMORY_COMPARE:
                return (rbx.getString("TypeMemoryCompare"));
            case Conditional.TYPE_FAST_CLOCK_RANGE:
                return (rbx.getString("TypeFastClockRange"));
            case Conditional.TYPE_SIGNAL_HEAD_RED:
                return (rbx.getString("TypeSignalHeadRed"));
            case Conditional.TYPE_SIGNAL_HEAD_YELLOW:
                return (rbx.getString("TypeSignalHeadYellow"));
            case Conditional.TYPE_SIGNAL_HEAD_GREEN:
                return (rbx.getString("TypeSignalHeadGreen"));
            case Conditional.TYPE_SIGNAL_HEAD_DARK:
                return (rbx.getString("TypeSignalHeadDark"));
            case Conditional.TYPE_SIGNAL_HEAD_FLASHRED:
                return (rbx.getString("TypeSignalHeadFlashRed"));
            case Conditional.TYPE_SIGNAL_HEAD_FLASHYELLOW:
                return (rbx.getString("TypeSignalHeadFlashYellow"));
            case Conditional.TYPE_SIGNAL_HEAD_FLASHGREEN:
                return (rbx.getString("TypeSignalHeadFlashGreen"));
            case Conditional.TYPE_SIGNAL_HEAD_LIT:
                return (rbx.getString("TypeSignalHeadLit"));
            case Conditional.TYPE_SIGNAL_HEAD_HELD:
                return (rbx.getString("TypeSignalHeadHeld"));
        }
        return ("");
    }

    public static String getCompareTypeString(int index) {
        switch(index) {
            case LESS_THAN:
                return (rbx.getString("LessThan"));
            case LESS_THAN_OR_EQUAL:
                return (rbx.getString("LessOrEqual"));
            case 0:
            case EQUAL:
                return (rbx.getString("Equal"));
            case GREATER_THAN_OR_EQUAL:
                return (rbx.getString("GreaterOrEqual"));
            case GREATER_THAN:
                return (rbx.getString("GreaterThan"));
        }
        return ("");
    }

    public static String getCompareSymbols(int index) {
        switch(index) {
            case LESS_THAN:
                return ("<");
            case LESS_THAN_OR_EQUAL:
                return ("<=");
            case 0:
            case EQUAL:
                return ("=");
            case GREATER_THAN_OR_EQUAL:
                return (">=");
            case GREATER_THAN:
                return (">");
        }
        return ("");
    }

    public String toString() {
        String type = getTypeString();
        switch (_type) {
            case Conditional.TYPE_NONE:
                return type;
            case Conditional.TYPE_SENSOR_ACTIVE:
            case Conditional.TYPE_SENSOR_INACTIVE:
                return java.text.MessageFormat.format(rbx.getString("VarStateDescrpt"),
                             new Object[] {rbx.getString("Sensor"), _name, type} );
            case Conditional.TYPE_TURNOUT_THROWN:
            case Conditional.TYPE_TURNOUT_CLOSED:
                return java.text.MessageFormat.format(rbx.getString("VarStateDescrpt"),
                             new Object[] {rbx.getString("Turnout"), _name, type} );
            case Conditional.TYPE_CONDITIONAL_TRUE:
            case Conditional.TYPE_CONDITIONAL_FALSE:
                return java.text.MessageFormat.format(rbx.getString("VarStateDescrpt"),
                             new Object[] {rbx.getString("Conditional"), _name, type} );
            case Conditional.TYPE_LIGHT_ON:
            case Conditional.TYPE_LIGHT_OFF:
                return java.text.MessageFormat.format(rbx.getString("VarStateDescrpt"),
                             new Object[] {rbx.getString("Light"), _name, type} );
            case Conditional.TYPE_MEMORY_EQUALS:
                return java.text.MessageFormat.format(rbx.getString("MemoryValueDescrpt"),
                             new Object[] {rbx.getString("Memory"), _name, 
                                           getCompareSymbols(_num1), _dataString} );
            case Conditional.TYPE_MEMORY_COMPARE:
                return java.text.MessageFormat.format(rbx.getString("MemoryCompareDescrpt"),
                             new Object[] {rbx.getString("Memory"), _name, 
                                           getCompareSymbols(_num1), _dataString} );
            case Conditional.TYPE_FAST_CLOCK_RANGE:
                return java.text.MessageFormat.format(rbx.getString("FastClockDescrpt"),
                             new Object[] {rbx.getString("FastClock"), 
                          LogixTableAction.formatTime(_num1 / 60, _num1 - ((_num1 / 60) * 60)),
                          LogixTableAction.formatTime(_num2 / 60, _num2 - ((_num2 / 60) * 60)) });
            case Conditional.TYPE_SIGNAL_HEAD_RED:
            case Conditional.TYPE_SIGNAL_HEAD_YELLOW:
            case Conditional.TYPE_SIGNAL_HEAD_GREEN:
            case Conditional.TYPE_SIGNAL_HEAD_DARK:
            case Conditional.TYPE_SIGNAL_HEAD_FLASHRED:
            case Conditional.TYPE_SIGNAL_HEAD_FLASHYELLOW:
            case Conditional.TYPE_SIGNAL_HEAD_FLASHGREEN:
                return java.text.MessageFormat.format(rbx.getString("SignalStateDescrpt"),
                             new Object[] {rbx.getString("Signal"), _name, type} );
            case Conditional.TYPE_SIGNAL_HEAD_LIT:
            case Conditional.TYPE_SIGNAL_HEAD_HELD:
                return java.text.MessageFormat.format(rbx.getString("VarStateDescrpt"),
                             new Object[] {rbx.getString("Signal"), _name, type} );
        }
        return "";
    }

	static final org.apache.log4j.Logger log = org.apache.log4j.Logger
		.getLogger(ConditionalVariable.class.getName());
}
