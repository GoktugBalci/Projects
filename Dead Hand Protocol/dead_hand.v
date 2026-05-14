`timescale 1s/1ms
// ============================================================
// dead_hand.v  (STUDENT STARTER CODE)
// BBM233 Logic Design Laboratory
// Final Project: World War III – Dead Hand Protocol
//
// IMPORTANT:
// - Do NOT change module name or port list.
// - clk is 1 Hz (1 tick per second).
// - reset is synchronous, active-high.
// - Implement Main FSM + Engagement Sub-FSM.
// ============================================================

module dead_hand(
    input  wire       clk,
    input  wire       reset,
    input  wire [1:0] threat_level,
    input  wire       diplomatic_override,
    input  wire       comms_lost,
    input  wire       system_fault,

    output reg        armed_out,
    output reg        tracking_out,
    output reg        authorization_out,
    output reg        override_ignored,
    output reg [2:0]  main_state_out,
    output reg [1:0]  sub_state_out,
    output reg [31:0] timer_out
);

    localparam [2:0] S_PEACE        = 3'b000;
    localparam [2:0] S_ALERT        = 3'b001;
    localparam [2:0] S_MOBILIZATION = 3'b010;
    localparam [2:0] S_ENGAGEMENT   = 3'b011;
    localparam [2:0] S_GLOBAL_WAR   = 3'b101;
    localparam [2:0] S_DEADLOCK     = 3'b110;

    localparam [1:0] SUB_ARM        = 2'b00;
    localparam [1:0] SUB_TRACK      = 2'b01;
    localparam [1:0] SUB_AUTHORIZE  = 2'b10;
    localparam [1:0] SUB_ABORT      = 2'b11;

    reg [31:0] timer_peace_to_alert;
    reg [31:0] timer_alert_to_mob;
    reg [31:0] timer_alert_to_peace;
    reg [31:0] timer_mob_to_alert;
    reg [31:0] sub_timer;

    always @(*) begin
        armed_out         = (sub_state_out == SUB_ARM);
        tracking_out      = (sub_state_out == SUB_TRACK);
        authorization_out = (sub_state_out == SUB_AUTHORIZE);

        timer_out = 32'b0;
        case (main_state_out)
            S_PEACE:        timer_out = timer_peace_to_alert;
            S_ALERT:        timer_out = (timer_alert_to_mob > 0) ? timer_alert_to_mob : timer_alert_to_peace;
            S_MOBILIZATION: timer_out = timer_mob_to_alert;
            S_ENGAGEMENT:   timer_out = sub_timer;
            default:        timer_out = 32'b0;
        endcase
    end

    always @(posedge clk) begin
        if (reset) begin
            main_state_out       <= S_PEACE;
            sub_state_out        <= SUB_ABORT; 
            override_ignored     <= 1'b0;
            timer_peace_to_alert <= 0;
            timer_alert_to_mob   <= 0;
            timer_alert_to_peace <= 0;
            timer_mob_to_alert   <= 0;
            sub_timer            <= 0;
        end
        else begin
            if (system_fault && (main_state_out != S_DEADLOCK) && (main_state_out != S_GLOBAL_WAR)) begin
                main_state_out <= S_GLOBAL_WAR;
                sub_state_out  <= SUB_ABORT;
                sub_timer      <= 0;
            end
            else begin
                if (diplomatic_override) begin
                    if (main_state_out == S_DEADLOCK || main_state_out == S_GLOBAL_WAR) begin
                        override_ignored <= 1'b1;
                    end
                    if ((main_state_out == S_ENGAGEMENT) && (sub_state_out == SUB_AUTHORIZE) && (sub_timer >= 2)) begin
                        override_ignored <= 1'b1;
                    end
                end

                case (main_state_out)
                    S_PEACE: begin
                        sub_state_out <= SUB_ABORT;
                        sub_timer     <= 0;

                        if (threat_level >= 2'b01) begin
                            if (timer_peace_to_alert >= 4) begin
                                main_state_out       <= S_ALERT;
                                timer_peace_to_alert <= 0;
                            end else begin
                                timer_peace_to_alert <= timer_peace_to_alert + 1;
                            end
                        end else begin
                            timer_peace_to_alert <= 0;
                        end
                    end

                    S_ALERT: begin
                        sub_state_out <= SUB_ABORT;
                        sub_timer     <= 0;

                        if (threat_level >= 2'b10) begin
                            timer_alert_to_peace <= 0;
                            if (timer_alert_to_mob >= 9) begin
                                main_state_out     <= S_MOBILIZATION;
                                timer_alert_to_mob <= 0;
                            end else begin
                                timer_alert_to_mob <= timer_alert_to_mob + 1;
                            end
                        end
                        else if (threat_level == 2'b00) begin
                            timer_alert_to_mob <= 0;
                            if (timer_alert_to_peace >= 3) begin
                                main_state_out       <= S_PEACE;
                                timer_alert_to_peace <= 0;
                            end else begin
                                timer_alert_to_peace <= timer_alert_to_peace + 1;
                            end
                        end
                        else begin
                            timer_alert_to_mob   <= 0;
                            timer_alert_to_peace <= 0;
                        end
                    end

                    S_MOBILIZATION: begin
                        if (comms_lost || (threat_level == 2'b11)) begin
                            main_state_out <= S_ENGAGEMENT;
                            sub_state_out  <= SUB_ARM;
                            sub_timer      <= 0;
                            timer_mob_to_alert <= 0;
                        end
                        else begin
                            if (threat_level <= 2'b01) begin
                                if (timer_mob_to_alert >= 3) begin
                                    main_state_out     <= S_ALERT;
                                    timer_mob_to_alert <= 0;
                                end else begin
                                    timer_mob_to_alert <= timer_mob_to_alert + 1;
                                end
                            end else begin
                                timer_mob_to_alert <= 0;
                            end
                            sub_state_out <= SUB_ABORT;
                            sub_timer     <= 0;
                        end
                    end

                    S_ENGAGEMENT: begin
                        if (diplomatic_override && !override_ignored && !(sub_state_out == SUB_AUTHORIZE && sub_timer >= 2)) begin
                             if (sub_state_out != SUB_ABORT) begin
                                 sub_state_out <= SUB_ABORT;
                                 sub_timer     <= 0;
                             end else begin
                                 sub_timer <= sub_timer + 1;
                             end
                        end
                        else begin
                            sub_timer <= sub_timer + 1;
                            case (sub_state_out)
                                SUB_ARM: begin
                                    if (sub_timer >= 4) begin
                                        sub_state_out <= SUB_TRACK;
                                        sub_timer     <= 0;
                                    end
                                end
                                SUB_TRACK: begin
                                    if (sub_timer >= 6) begin
                                        sub_state_out <= SUB_AUTHORIZE;
                                        sub_timer     <= 0;
                                    end
                                end
                                SUB_AUTHORIZE: begin
                                    if (sub_timer >= 3) begin
                                        main_state_out <= S_DEADLOCK;
                                    end
                                end
                                SUB_ABORT: begin
                                    if (sub_timer >= 5) begin
                                        main_state_out <= S_MOBILIZATION;
                                    end
                                end
                            endcase
                        end
                    end

                    S_GLOBAL_WAR: begin
                        sub_state_out <= SUB_ABORT;
                        sub_timer     <= 0;
                    end

                    S_DEADLOCK: begin
                        sub_state_out <= SUB_ABORT;
                        sub_timer     <= 0;
                    end

                    default: main_state_out <= S_PEACE;
                endcase
            end
        end
    end

// Your code starts from here.
endmodule