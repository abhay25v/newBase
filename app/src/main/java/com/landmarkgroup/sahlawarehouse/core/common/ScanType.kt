package com.landmarkgroup.sahlawarehouse.core.common

/**
 * Direct port of `SahlaWH.UtilityManager.Enum.ScanType` - identifies each home-screen tile /
 * feature module. Used to route scroll-menu tile taps to the correct destination.
 */
enum class ScanType {
    PalletBuilding,
    Handover,
    PTW,
    AuditHelper,
    Dispatch,
    Return,
    Reinduction,
    Appointment,
    ReturnInbound,
    ReturnsReceive,
    CartonCancellation,
    PalletIDPrinting,
    ToteToCarton,
    TrackAndTrace,
    MfcPTW,
    MfcSortation,
    ExcessOrUnexpectedCarton,
    ImageCapture,
    BuildPallet,
    MovePallet,
    CreateTrailer,
    LoadTrailer,
    UnloadTrailer,
    Search,
    PrintUtility,
    ItemLookup,
    DeviceManagement,
    CasePick,
    MultiStorePick,
    BulkPicking,
    RFIDASN
}
