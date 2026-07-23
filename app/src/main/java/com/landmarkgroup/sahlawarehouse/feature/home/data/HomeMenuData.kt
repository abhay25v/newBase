package com.landmarkgroup.sahlawarehouse.feature.home.data

import com.landmarkgroup.sahlawarehouse.core.common.BottomTabMenu
import com.landmarkgroup.sahlawarehouse.core.common.ScanType
import com.landmarkgroup.sahlawarehouse.core.common.ScrollMenuItem

object HomeMenuData {

    val AUTHORIZED_ROLES = listOf(
        "store_manager", "store_associate", "store_managers", "store_associates",
        "store_supervisors", "store_officials", "area_managers", "retail_ops",
        "warehouse_managers", "warehouse_supervisors", "warehouse_associates"
    )

    val defaultScrollMenu: List<ScrollMenuItem> = listOf(
        ScrollMenuItem(ScanType.PalletBuilding, "Build Dolly/Pallet", "Speedy Handover"),
        ScrollMenuItem(ScanType.Handover, "Handover", "Scan cartons and move to staging area", "#F8B36E", "#FA696D", "#FFFFFF"),
        ScrollMenuItem(ScanType.PTW, "Put To Wall", "Scan Carton, Item and place inside the tote"),
        ScrollMenuItem(ScanType.AuditHelper, "Audit Assistant", "Scan tote for movement"),
        ScrollMenuItem(ScanType.Reinduction, "C&C Sortation", "Storewise Sortation"),
        ScrollMenuItem(ScanType.Dispatch, "Order Dispatch", "Scan pallet or carton"),
        ScrollMenuItem(ScanType.Appointment, "Appointments", "Return Manifest"),
        ScrollMenuItem(ScanType.ReturnInbound, "Returns Inbound", "To Conloc.."),
        ScrollMenuItem(ScanType.ReturnsReceive, "Returns Receive", "To Conloc.."),
        ScrollMenuItem(ScanType.CartonCancellation, "Cancellation", "Customer Carton Cancellation"),
        ScrollMenuItem(ScanType.PrintUtility, "Print Utility", "Print Utility")
    )

    val defaultTabMenu: List<BottomTabMenu> = listOf(
        BottomTabMenu(0, "e-Fullfill", isSelected = true),
        BottomTabMenu(1, "More")
    )
}
