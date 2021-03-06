Feature: Test renaming syntax tree variables

  Scenario: Syntax tree rename basic
    Given Non-renamed script:
    """
    globals
    hashtable nzHash=InitHashtable()
    endglobals
    """
    When Tree is renamed from "nzHash" to "nzHashTwo"
    Then Renamed script should be:
    """
    globals
    hashtable nzHashTwo=InitHashtable()
    endglobals
    """

  Scenario: Syntax tree rename with array
    Given Non-renamed script:
    """
    globals
    boolean array ybx
    endglobals
    function sla takes integer Zbx,string sLa returns nothing
      if not (ybx[Zbx]) then
          set ybx[Zbx] = true
          set yBx[Zbx] = aao(Zbx)
          set ycx[Zbx] = Xno()
          set yCx[Zbx] = Xqo()
          set ydx[Zbx] = Dyo()
          set yDx[Zbx] = Dyo()
          set yfx[Zbx] = 0
          set yFx[Zbx] = GetRandomInt(1,1000)
          set ygx[Zbx] = sLa
          if StringLength(ygx[Zbx]) == 0 then
              set ygx[Zbx] = "SaveFile" + I2S(GetRandomInt(1,10000))
          endif
          call XOo(XOo(XOo(XOo(XOo(XOo(XOo(XOo(XOo(XOo(ycx[Zbx],1),2),3),12),14),15),21),50),13),11)
          call sHa(Zbx)
          call TimerStart(yBx[Zbx],0.,false,function sKa)
          call fso(Zbx,"Saving has begun. Please refrain from any activity until the saving is complete.")
      endif
    endfunction
    """
    When Tree is renamed from "ybx" to "test123"
    Then Renamed script should be:
    """
    globals
    boolean array test123
    endglobals
    function sla takes integer Zbx,string sLa returns nothing
    if not ((test123[Zbx])) then
    set test123[Zbx] = true
    set yBx[Zbx] = aao(Zbx)
    set ycx[Zbx] = Xno()
    set yCx[Zbx] = Xqo()
    set ydx[Zbx] = Dyo()
    set yDx[Zbx] = Dyo()
    set yfx[Zbx] = 0
    set yFx[Zbx] = GetRandomInt(1,1000)
    set ygx[Zbx] = sLa
    if StringLength(ygx[Zbx]) == 0 then
    set ygx[Zbx] = "SaveFile" + I2S(GetRandomInt(1,10000))
    endif
    call XOo(XOo(XOo(XOo(XOo(XOo(XOo(XOo(XOo(XOo(ycx[Zbx],1),2),3),12),14),15),21),50),13),11)
    call sHa(Zbx)
    call TimerStart(yBx[Zbx],0.,false,function sKa)
    call fso(Zbx,"Saving has begun. Please refrain from any activity until the saving is complete.")
    endif
    endfunction
    """

  Scenario: Syntax tree rename function
    Given Non-renamed script:
    """
    globals
    hashtable nzHash=InitHashtable()
    endglobals
    function SaveUnit takes string HashName, unit Target returns nothing
    call RemoveSavedHandle(nzHash, GlobalHandle( ), StringHash( HashName ) )
    call SaveUnitHandle(nzHash, GlobalHandle( ), StringHash( HashName ), Target )
    endfunction
    """
    When Tree is renamed from "nzHash" to "nzHashTwo"
    Then Renamed script should be:
    """
    globals
    hashtable nzHashTwo=InitHashtable()
    endglobals
    function SaveUnit takes string HashName,unit Target returns nothing
    call RemoveSavedHandle(nzHashTwo,GlobalHandle(),StringHash(HashName))
    call SaveUnitHandle(nzHashTwo,GlobalHandle(),StringHash(HashName),Target)
    endfunction
    """

  Scenario: Syntax tree rename local
    Given Non-renamed script:
    """
    globals
    endglobals
    function ArrowAct takes nothing returns nothing
    local integer i=LoadInteger( nzHash, GetHandleId( GetTriggerPlayer( ) ), StringHash( "Lenght" ) )
    local eventid aid=GetTriggerEventId( )
    if SubString(LoadStr(nzHashTwo,GlobalHandle(),StringHash("ArrowSequence")),i,i + 1) == LoadStr(nzHashTwo,GlobalHandle(),GetHandleId(aid)) then
    if i == StringLength(LoadStr(nzHashTwo,GlobalHandle(),StringHash("ArrowSequence"))) - 1 then
    call ActEvent(GetPlayerId(GetTriggerPlayer()))
    call SaveInteger(nzHashTwo,GetHandleId(GetTriggerPlayer()),StringHash("Lenght"),0)
    else
    call SaveInteger(nzHashTwo,GetHandleId(GetTriggerPlayer()),StringHash("Lenght"),i + 1)
    endif
    else
    call SaveInteger(nzHashTwo,GetHandleId(GetTriggerPlayer()),StringHash("Lenght"),0)
    endif
    endfunction
    """
    When Tree is renamed from "nzHash" to "nzHashTwo"
    Then Renamed script should be:
    """
    globals
    endglobals
    function ArrowAct takes nothing returns nothing
    local integer i=LoadInteger(nzHashTwo,GetHandleId(GetTriggerPlayer()),StringHash("Lenght"))
    local eventid aid=GetTriggerEventId()
    if SubString(LoadStr(nzHashTwo,GlobalHandle(),StringHash("ArrowSequence")),i,i + 1) == LoadStr(nzHashTwo,GlobalHandle(),GetHandleId(aid)) then
    if i == StringLength(LoadStr(nzHashTwo,GlobalHandle(),StringHash("ArrowSequence"))) - 1 then
    call ActEvent(GetPlayerId(GetTriggerPlayer()))
    call SaveInteger(nzHashTwo,GetHandleId(GetTriggerPlayer()),StringHash("Lenght"),0)
    else
    call SaveInteger(nzHashTwo,GetHandleId(GetTriggerPlayer()),StringHash("Lenght"),i + 1)
    endif
    else
    call SaveInteger(nzHashTwo,GetHandleId(GetTriggerPlayer()),StringHash("Lenght"),0)
    endif
    endfunction
    """


  Scenario: Syntax tree rename function basic
    Given Non-renamed script:
    """
    globals
    hashtable nzHash = InitHashTable()
    endglobals
    function LoadUnit takes string HashName returns unit
    return LoadUnitHandle(nzHash, GlobalHandle( ), StringHash( HashName ) )
    endfunction
    function SelectedUnit takes player LocPlayer returns unit
    call GroupEnumUnitsSelected( EnumUnits( ), LocPlayer, null )
    call SaveUnit( "SelectedUnit", FirstOfGroup( EnumUnits( ) ) )
    call GroupClear( EnumUnits( ) )
    return LoadUnit( "SelectedUnit" )
    endfunction
    """
    When Tree is function renamed from "LoadUnit" to "LoadUnitCustom"
    Then Renamed script should be:
    """
    globals
    hashtable nzHash=InitHashTable()
    endglobals
    function LoadUnitCustom takes string HashName returns unit
    return LoadUnitHandle(nzHash,GlobalHandle(),StringHash(HashName))
    endfunction
    function SelectedUnit takes player LocPlayer returns unit
    call GroupEnumUnitsSelected(EnumUnits(),LocPlayer,null)
    call SaveUnit("SelectedUnit",FirstOfGroup(EnumUnits()))
    call GroupClear(EnumUnits())
    return LoadUnitCustom("SelectedUnit")
    endfunction
    """

  Scenario: Syntax tree rename function with call
    Given Non-renamed script:
    """
    globals
    hashtable nzHash=InitHashTable()
    endglobals
    function LumberRating takes nothing returns nothing
    call SaveInteger(nzHash,HandleID,StringHash("CurrentLumber"),GetPlayerState(GetTriggerPlayer(),PLAYER_STATE_RESOURCE_LUMBER))
    endfunction
    function nzInit takes nothing returns nothing
    call PlayerStateEvent(LoadTrig("LumbRateTrig"),PLAYER_STATE_RESOURCE_LUMBER,function LumberRating)
    call PlayerStateEvent(LoadTrig("GoldRateTrig"),PLAYER_STATE_RESOURCE_GOLD,function GoldRating)
    endfunction
    """
    When Tree is function renamed from "LumberRating" to "LumberRatingCustom"
    Then Renamed script should be:
    """
    globals
    hashtable nzHash=InitHashTable()
    endglobals
    function LumberRatingCustom takes nothing returns nothing
    call SaveInteger(nzHash,HandleID,StringHash("CurrentLumber"),GetPlayerState(GetTriggerPlayer(),PLAYER_STATE_RESOURCE_LUMBER))
    endfunction
    function nzInit takes nothing returns nothing
    call PlayerStateEvent(LoadTrig("LumbRateTrig"),PLAYER_STATE_RESOURCE_LUMBER,function LumberRatingCustom)
    call PlayerStateEvent(LoadTrig("GoldRateTrig"),PLAYER_STATE_RESOURCE_GOLD,function GoldRating)
    endfunction
    """















